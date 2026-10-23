package application.adapters.web;

/**
 * HTTP headers shared by every controller.
 * USER_ID identifies the authenticated participant that executes the operation (RG-01).
 */
public final class RequestHeaders {

    public static final String USER_ID = "X-User-Id";

    private RequestHeaders() {}
}
