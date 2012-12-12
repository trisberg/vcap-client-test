package com.springdeveloper.vcap;

import java.io.Console;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.cloudfoundry.client.lib.rest.CloudControllerClientV1;
import org.cloudfoundry.client.lib.util.RestUtil;

/**
 * Simple test of CLoud Foundry VCAP connectivity.
 *
 * @author Thomas Risberg
 */
public class VcapClientTest {
	
	private static final String CC_URL = System.getProperty("vcap.target", "https://api.cloudfoundry.com");
	private static String vcap_email = System.getProperty("vcap.email");
	private static String vcap_passwd = System.getProperty("vcap.passwd");
	private static final String uaa_url = System.getProperty("uaa.url", "https://uaa.cloudfoundry.com");

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
		System.out.println("Authenticating against: " + uaa_url);
		
		CloudCredentials credentials = new CloudCredentials(vcap_email, vcap_passwd);

		CloudControllerClient ccc = null;
		
		try {
			ccc= new CloudControllerClientV1(new URL(CC_URL), new RestUtil(), credentials, new URL(uaa_url), null);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		ccc.login();
		
		List<CloudApplication> apps = ccc.getApplications();

		System.out.println("Apps:");
		for (CloudApplication app : apps) {
			System.out.println("  " + app.getName() + " " + app.getState());
		}

		String logs = ccc.getFile("mongosv-rest", 0, "/logs", -1, -1);
		System.out.println(logs);
		System.out.println(printBytes(logs.getBytes()));

		System.out.println(printBytes(System.getProperty("line.separator").getBytes()));

		String separator = new String(new byte[] {0xa});

		System.out.println(printBytes(separator.getBytes()));

		String[] lines1 = logs.split(System.getProperty("line.separator"));
		System.out.println(Arrays.asList(lines1));

		String[] lines2 = logs.split(separator);
		System.out.println(Arrays.asList(lines2));

		ccc.logout();

	}

	private static String printBytes(byte[] array) {
		StringBuilder printable = new StringBuilder();
		printable.append("[" + array.length + "] = " + "0x");
		for (int k = 0; k < array.length; k++) {
			printable.append(byteToHex(array[k]));
		}
		return printable.toString();
	}

	private static String byteToHex(byte b) {
		// Returns hex String representation of byte b
		char hexDigit[] = {
				'0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
		};
		char[] array = {hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f]};
		return new String(array);
	}

}
