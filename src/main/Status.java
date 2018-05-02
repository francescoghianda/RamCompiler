package main;

public class Status
{
    private int status;
    private int line;
    private String message;

    public Status(int status, int line, String message)
    {
        this.status = status;
        this.line = line;
        this.message = message;
    }

    public Status set(int status, int line, String message)
    {
        this.status = status;
        this.line = line;
        this.message = message;
        return this;
    }

    public int getStatus()
    {
        return this.status;
    }

    public int getLine()
    {
        return this.line;
    }

    public String getMessage()
    {
        return this.message;
    }
}
