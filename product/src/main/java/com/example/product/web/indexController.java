package com.example.product.web;

import com.example.product.entity.CategoryEntity;
import com.example.product.service.CategoryService;
import com.example.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author sally
 * @date 2022-09-21 9:13
 */
@Controller
public class indexController {

	@Resource
	CategoryService categoryService;

	@Resource
	RedissonClient redissonClient;

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@GetMapping({"/", "index.html"})
	public String index(Model model) {
		List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categories();
		model.addAttribute("categories", categoryEntityList);
		return "index";
	}

	@ResponseBody
	@GetMapping("index/catalog.json")
	public Map<String, List<Catelog2Vo>> catelogJson() {
		return categoryService.getCatelogJson();
	}

	@GetMapping("/hello")
	public String hello() {
		RLock lock = redissonClient.getLock("mylock");
		// lock.lock(); // 阻塞式等待，默认加的锁都是30s
		lock.lock(10, TimeUnit.SECONDS); // 最佳实战，省掉续期操作，手动解锁
		try {
			System.out.println("加锁成功，执行业务。。。" + Thread.currentThread().getId());
			Thread.sleep(30000);
		} catch (Exception ignored) {

		} finally {
			System.out.println("释放锁...");
			lock.unlock(); // attempt to unlock lock, not locked by current thread by node id
		}
		return "hello";
	}


	@GetMapping("/write")
	@ResponseBody
	public String writeValue() {
		RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
		RLock rLock = lock.writeLock();
		String s = "";
		try {
			rLock.lock();
			System.out.println("写锁加锁成功。。。" + Thread.currentThread().getId());
			s = UUID.randomUUID().toString();
			Thread.sleep(20000);
			stringRedisTemplate.opsForValue().set("writeValue", s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rLock.unlock();
			System.out.println("写锁释放。。。" + Thread.currentThread().getId());
		}
		return s;
	}

	@GetMapping("/read")
	@ResponseBody
	public String readValue() {
		RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
		RLock rLock = lock.readLock();
		rLock.lock();
		String s = "";
		try {
			System.out.println("读锁加锁成功。。。" + Thread.currentThread().getId());
			Thread.sleep(20000);
			s = stringRedisTemplate.opsForValue().get("writeValue");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rLock.unlock();
			System.out.println("读锁释放。。。" + Thread.currentThread().getId());
		}

		return s;
	}

	@GetMapping("/park")
	@ResponseBody
	public String park() throws InterruptedException {
		RSemaphore park = redissonClient.getSemaphore("park");
		// park.acquire();
		boolean b = park.tryAcquire();
		return "ok=>"+b;
	}

	@GetMapping("/go")
	@ResponseBody
	public String go(){
		RSemaphore park = redissonClient.getSemaphore("park");
		park.release();
		return "ok";
	}

	@GetMapping("/lockdoor")
	@ResponseBody
	public String lockDoor() throws InterruptedException {
		RCountDownLatch door = redissonClient.getCountDownLatch("door");
		door.trySetCount(5);
		door.await(); // 等待闭锁都完成
		return "放假了";
	}

	@GetMapping("/gogogo/{id}")
	@ResponseBody
	public String gogogo(@PathVariable("id") Long id){
		RCountDownLatch door = redissonClient.getCountDownLatch("door");
		door.countDown(); // 计数减一
		return id + " 班的人走了。。。";
	}
}
