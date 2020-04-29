package org.adv25.ADVNTRIP.Tools;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;

public class HttpRequestParser {
    private String requestLine;
    private Hashtable<String, String> requestHeaders;
    private StringBuffer _messageBody;

    public HttpRequestParser() {
        requestHeaders = new Hashtable<String, String>();
        _messageBody = new StringBuffer();
    }

    public void parseRequest(String request) throws IOException, HttpFormatException, IndexOutOfBoundsException {
        BufferedReader reader = new BufferedReader(new StringReader(request));

        setRequestLine(reader.readLine());

        String header = reader.readLine();
        while (header.length() > 0) {
            appendHeaderParameter(header);
            header = reader.readLine();
        }

        String bodyLine = reader.readLine();
        while (bodyLine != null) {
            appendMessageBody(bodyLine);
            bodyLine = reader.readLine();
        }

        String[] splitedLine = requestLine.split(" ");

        if (requestLine.matches("GET [\\S]+ HTTP[\\S]+")) {
                requestHeaders.put("GET", splitedLine[1].replaceAll("/", ""));
        }

        if (requestLine.matches("SOURCE [\\S]+ [\\S]+")) {
                requestHeaders.put("SOURCE", splitedLine[2]);
                requestHeaders.put("PASSWORD", splitedLine[1]);
        }
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getMessageBody() {
        return _messageBody.toString();
    }

    private void setRequestLine(String requestLine) throws HttpFormatException {
        if (requestLine == null || requestLine.length() == 0) {
            throw new HttpFormatException("Invalid Request-Line: " + requestLine);
        }
        this.requestLine = requestLine;
    }

    private void appendHeaderParameter(String header) throws HttpFormatException {
        int idx = header.indexOf(":");
        if (idx == -1) {
            throw new HttpFormatException("Invalid Header Parameter: " + header);
        }
        requestHeaders.put(header.substring(0, idx), header.substring(idx + 1, header.length()));
    }

    private void appendMessageBody(String bodyLine) {
        _messageBody.append(bodyLine).append("\r\n");
    }

    public String getParam(String headerName) {
        return requestHeaders.get(headerName);
    }

    public void setRemoteAddress(String ip){
        requestHeaders.put("ip", ip);
    }

}
class HttpFormatException2 extends Throwable {
    public HttpFormatException2(String s) {
        System.out.println(s);
    }
}
