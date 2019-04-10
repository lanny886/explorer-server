package com.xyz.browser.app.core.mail;

public interface MailService {
    String SUBJECT_KEEP_ALIVE="同步中断";
    void send(String to, String subject, String text);
}
