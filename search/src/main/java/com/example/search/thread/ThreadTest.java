package com.example.search.thread;

import java.util.concurrent.*;

/**
 * @author sally
 * @date 2022-10-08 9:55
 */
public class ThreadTest {
	public static ExecutorService executor = Executors.newFixedThreadPool(10);

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("main start...");
		// CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
		// 	System.out.println("runAsync current thread: " + Thread.currentThread().getId());
		// 	int i = 10 / 2;
		// 	System.out.println("result: " + i);
		// }, executor);
		// CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
		// 	System.out.println("supplyAsync current thread: " + Thread.currentThread().getId());
		// 	int i = 10 / 0;
		// 	System.out.println("result: " + i);
		// 	return i;
		// }, executor).whenComplete((res, exception) -> {
		// 	System.out.println("async complete: result->" + res + "; exception->" + exception);
		// }).exceptionally(t->{
		// 	return 10;
		// });
		// CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
		// 	System.out.println("supplyAsync current thread: " + Thread.currentThread().getId());
		// 	int i = 10 / 5;
		// 	System.out.println("result: " + i);
		// 	return i;
		// }, executor).handle((res, thr) -> {
		// 	if (res != null) {
		// 		return res * 2;
		// 	}
		// 	if (thr != null) {
		// 		return 0;
		// 	}
		// 	return 10;
		// });
		/**
		 * 线程串行化方法
		 *
		 */
		// 不能获取上一步执行结果
		// CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
		// 	System.out.println("supplyAsync current thread: " + Thread.currentThread().getId());
		// 	int i = 10 / 5;
		// 	System.out.println("result: " + i);
		// 	return i;
		// }, executor).thenRunAsync(()->{
		// 	System.out.println("task 2 start...");
		// },executor);
		// 可以获取上一步执行结果,但无返回值
		// CompletableFuture.supplyAsync(() -> {
		// 	System.out.println("current thread: " + Thread.currentThread().getId());
		// 	int i = 10 / 5;
		// 	System.out.println("result: " + i);
		// 	return i;
		// }, executor).thenAcceptAsync((res)->{
		// 	System.out.println("task 2 start... " + res);
		// },executor);
		// 可以获取上一步执行结果,有返回值
		// CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
		// 	System.out.println("current thread: " + Thread.currentThread().getId());
		// 	int i = 10 / 5;
		// 	System.out.println("result: " + i);
		// 	return i;
		// }, executor).thenApplyAsync(res -> {
		// 	System.out.println("task 2 start..." + res);
		// 	return "thenApplyAsync result: " + res;
		// }, executor);
		// CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
		// 	System.out.println("task 1 start...");
		// 	System.out.println("task 1 end...");
		// 	return 2;
		// }, executor);
		//
		// CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
		// 	System.out.println("task 2 start...");
		// 	try {
		// 		Thread.sleep(2000);
		// 		System.out.println("task 2 end...");
		// 	} catch (InterruptedException e) {
		// 		e.printStackTrace();
		// 	}
		// 	return "future02";
		// }, executor);

		// future01.runAfterBothAsync(future02,()->{
		// 	System.out.println("task 3 start...");
		// },executor);
		// future01.thenAcceptBothAsync(future02, (f1, f2) -> {
		// 	System.out.println("task 3 start..." + f1 + "; ->" + f2);
		// }, executor);
		// CompletableFuture<String> future = future01.thenCombineAsync(future02, (f1, f2) -> {
		// 	System.out.println("task 3 start...");
		// 	return f1 + "->" + f2 + "->" + "task3 result";
		// }, executor);
		/**
		 * 两任务只要一个完成
		 */
		// future01.runAfterEitherAsync(future02, () -> {
		// 	System.out.println("either 1 complete, task 3 start... ");
		// }, executor);
		// future01.acceptEitherAsync(future02, res -> {
		// 	System.out.println("either 1 complete, task 3 start... " + res);
		// }, executor);
		// CompletableFuture<String> future = future01.applyToEitherAsync(future02, res -> {
		// 	System.out.println("either 1 complete, task 3 start... " + res);
		// 	return res + "-> task3 result";
		// }, executor);
		/**
		 * 多任务组合
		 */
		CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
			System.out.println("get product img...");
			return "apple.jpg";
		}, executor);
		CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
			System.out.println("get product attr...");
			return "green+256g";
		}, executor);
		CompletableFuture<String> futureInfo = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(3000);
				System.out.println("get product info...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "autumn apple";
		}, executor);
		// CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureAttr, futureInfo);
		// allOf.get();
		// System.out.println(futureImg.get());
		// System.out.println(futureAttr.get());
		// System.out.println(futureInfo.get());
		CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureInfo);
		System.out.println(anyOf.get());
		// String result = future.get();
		System.out.println("main end...");
		// System.out.println("main end..." + result);
	}

	public void thread(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("main start...");
		// Thread01 thread01 = new Thread01();
		// thread01.start();
		// Runable01 runable01 = new Runable01();
		// new Thread(runable01).start();
		// FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
		// new Thread(futureTask).start();
		// // 阻塞等待线程运行结果
		// Integer result = futureTask.get();
		// System.out.println("main end..." + result);

		// service.execute(new Runable01());
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				5,
				200,
				10,
				TimeUnit.SECONDS,
				new LinkedBlockingDeque<>(10000),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.AbortPolicy());

		System.out.println("main end...");
	}

	public static class Callable01 implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			System.out.println("Callable01 current thread: " + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("result: " + i);
			return i;
		}
	}

	public static class Runable01 implements Runnable {

		@Override
		public void run() {
			System.out.println("Runable01 current thread: " + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("result: " + i);
		}
	}

	public static class Thread01 extends Thread {
		@Override
		public void run() {
			System.out.println("Thread01 current thread: " + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("result: " + i);
		}
	}
}
