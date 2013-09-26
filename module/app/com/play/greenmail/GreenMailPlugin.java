package com.play.greenmail;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import play.Application;
import play.Configuration;
import play.Logger;
import play.Plugin;

import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.store.Store;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class GreenMailPlugin extends Plugin {

	private final Application application;
	private GreenMail greenMail;

	public GreenMailPlugin(Application application) {
		this.application = application;
	}

	@Override
	public boolean enabled() {
		Configuration configuration = application.configuration();
		boolean disabled = configuration
				.getBoolean("greenmail.disabled", false);
		return !disabled;
	}

	@Override
	public void onStart() {
		super.onStart();
		Configuration configuration = application.configuration();
		int port = configuration.getInt("play.mail.port", 3025);
		ServerSetup setup = new ServerSetup(port, null, "smtp");
		greenMail = new GreenMail(setup);
		greenMail.start();
		Logger.info("greenmail plugin started");
	}

	@Override
	public void onStop() {
		super.onStop();
		greenMail.stop();
		Logger.info("greenmail plugin stopped");
	}

	public List<MimeMessage> getAllMessages() {
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		List<MimeMessage> listMessages = Arrays.asList(receivedMessages);
		return listMessages;
	}

	public List<MimeMessage> getAllMessagesSorted() {
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		List<MimeMessage> listMessages = Arrays.asList(receivedMessages);
		Collections.sort(listMessages, new Comparator<MimeMessage>() {
			public int compare(MimeMessage m1, MimeMessage m2) {
				Date m1Date = null;
				Date m2Date = null;
				try {
					m1Date = m1.getSentDate();
					m2Date = m2.getSentDate();
				} catch (MessagingException ex) {
					throw new RuntimeException();
				}
				if (m1Date != null && m2Date != null) {
					return m1Date.compareTo(m2Date);
				} else if (m1Date == null) {
					return -1;
				} else if (m2Date == null) {
					return 1;
				}
				return 0;
			}
		});
		return listMessages;
	}

	public MimeMessage getMessage(int id) {
		if (id < 0) {
			return null;
		}
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		if (id >= receivedMessages.length) {
			return null;
		}
		return receivedMessages[id];
	}

	public void clearMessages() {
		try {
			Object instance = greenMail.getManagers().getImapHostManager();
			Field field = instance.getClass().getDeclaredField("store");
			field.setAccessible(true);
			Store value = (Store) field.get(instance);
			Collection<MailFolder> mailBoxes = value.listMailboxes("*");
			if (!mailBoxes.isEmpty()) {
				for (MailFolder mailBox : mailBoxes) {
					mailBox.deleteAllMessages();
				}
			}
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

}
