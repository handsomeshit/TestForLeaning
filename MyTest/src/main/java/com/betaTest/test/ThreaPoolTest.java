package com.betaTest.test;

public class ThreaPoolTest {

//    public static void main(String[] arg) {
//
//        ThreadPoolExecutor threadPoolTaskExecutor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));
//        List<Callable<Boolean>> callableList = new ArrayList<>();
//        for(int i=0; i<20; i++) {
//            int finalI = i;
//            String test = "这是一段测试文字";
//            Callable<Boolean> callable = () -> {
//                //synchronized (test) {
//                    if(finalI%2 ==0) {
//                        //System.out.println("等待任务开始执行");
//                        //test.wait();
//                        //System.out.println("等待任务执行结束");
//                        System.out.println("运行到出错任务");
//                        throw new InterruptedException();
//                        //Thread.sleep(5000);
//                        //System.out.println("5秒线程执行完毕");
//                    } else {
//                        System.out.println("短时线程执行完毕");
//                    }
//                //}
//                return true;
//            };
//            callableList.add(callable);
//        }
//
//        try {
//            //执行任务
//            List<Future<Boolean>> futureList = threadPoolTaskExecutor.invokeAll(callableList);
//            //等待任务完成
//            for (Future<Boolean> future : futureList) {
//                future.get();
//            }
//            //关闭线程池
//            threadPoolTaskExecutor.shutdown();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException("出错");
//        }
//    }
}