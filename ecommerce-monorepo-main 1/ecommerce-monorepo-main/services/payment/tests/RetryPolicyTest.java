public class RetryPolicyTest {
    @Test
    public void shouldRetryForTransientErrors() {
        RetryPolicy policy = new RetryPolicy();
        assertTrue(policy.shouldRetry(1, new TimeoutException()));
    }
}
