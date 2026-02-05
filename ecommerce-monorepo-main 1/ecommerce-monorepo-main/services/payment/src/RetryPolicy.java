public class RetryPolicy {

    private int maxRetries = 3;
    private long baseDelayMs = 200;

    public boolean shouldRetry(int attempt, Exception e) {
        return attempt < maxRetries && isTransient(e);
    }

    public long getBackoffDelay(int attempt) {
        return baseDelayMs * (long)Math.pow(2, attempt);
    }

    private boolean isTransient(Exception e) {
        return e instanceof TimeoutException;
    }
}
