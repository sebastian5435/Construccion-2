package application.domain.models;

public class Refund {
    private String id;
    private Return returnProcess;
    public Refund() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Return getReturnProcess() { return returnProcess; }
    public void setReturnProcess(Return returnProcess) { this.returnProcess = returnProcess; }
}