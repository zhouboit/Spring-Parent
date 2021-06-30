package com.jonbore.wechat;

public class ExceptionTest {
    public static void main(String[] args) {
        try {
            System.out.println(run());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int run() throws Exception {
        int result = 2;
        try {
            if (System.currentTimeMillis() % 2 == 0) {
                throw new RuntimeException("exception");
            }
            result = 1;
        } catch (Exception e) {
            result = -1;
            e.printStackTrace();
            throw new Exception("catch", e);
        } finally {
            result = 3;
        }
        return result;
    }
}
