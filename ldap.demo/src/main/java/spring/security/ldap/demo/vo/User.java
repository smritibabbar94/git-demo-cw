package spring.security.ldap.demo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private String uid;
    private String cn;
    private String sn;

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getCn() { return cn; }
    public void setCn(String cn) { this.cn = cn; }
    public String getSn() { return sn; }
    public void setSn(String sn) { this.sn = sn; }
}
