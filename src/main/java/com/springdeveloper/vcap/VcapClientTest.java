package com.springdeveloper.vcap;

import java.io.Console;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;

/**
 * Simple test of CLoud Foundry VCAP connectivity.
 *
 * @author Thomas Risberg
 */
public class VcapClientTest {
	
	private static final String CC_URL = System.getProperty("vcap.target", "https://api.cloudfoundry.com");
	private static String vcap_email = System.getProperty("vcap.email");
	private static String vcap_passwd = System.getProperty("vcap.passwd");

	public static void main(String[] args) {

		Console console = System.console();
		//read user name, using java.util.Formatter syntax :
		if (vcap_email == null) {
			vcap_email = console.readLine("Login E-Mail? ");
        }
		//read the password, without echoing the output
		if (vcap_passwd == null) {
			vcap_passwd = new String(console.readPassword("Password? "));
        }
		
		System.out.println("Running tests on " + CC_URL + " on behalf of " + vcap_email);

		CloudFoundryClient client = null;
		try {
			client = new CloudFoundryClient(new CloudCredentials(vcap_email, vcap_passwd), new URL(CC_URL));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		client.login();
		
		List<CloudApplication> apps = client.getApplications();
		
		System.out.println("Apps:");
		for (CloudApplication app : apps) {
			System.out.println("  " + app.getName() + " " + app.getState());
		}
		
		client.logout();

	}
}
