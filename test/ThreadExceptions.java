public class ThreadExceptions {

  private static void expect(boolean v) {
    if (! v) throw new RuntimeException("Expectation failed");
  }

  private static class Handler implements Thread.UncaughtExceptionHandler {
    public String message;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        message = e.getMessage();
    }
  }

  public static void main(String[] args) throws Exception {
    { Thread thread = new Thread() {
        @Override
        public void run() {
          throw new RuntimeException("TEST-DEFAULT-HANDLER");
        }
      };

      Handler handler = new Handler();
      Thread.setDefaultUncaughtExceptionHandler(handler);
      thread.start();
      thread.join();

      expect("TEST-DEFAULT-HANDLER".equals(handler.message));
    }

    Thread.setDefaultUncaughtExceptionHandler(null);

    { Thread thread = new Thread() {
        @Override
        public void run() {
          throw new RuntimeException("TEST-HANDLER");
        }
      };

      Handler handler = new Handler();
      thread.setUncaughtExceptionHandler(handler);
      thread.start();
      thread.join();

      expect("TEST-HANDLER".equals(handler.message));
    }

  }
}
