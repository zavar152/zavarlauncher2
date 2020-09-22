package zavar.application;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import zavar.mojang.AuthenticationException;
import zavar.mojang.LoginService;
import zavar.mojang.Session;
import zavar.mojang.YggdrasilLoginService;

public class Authentication 
{
	private static Session current_session;
	
	public void login(String id, String password) throws AuthenticationException, IOException, InterruptedException
	{
		LoginService service = getLoginService();
		List<? extends Session> identities = service.login("Minecraft", id, password);
        if (identities.size() > 0) 
        {
        	current_session = identities.get(0);
        }
        else 
        {
            throw new AuthenticationException("Minecraft not owned", "ru_locale");
        }
	}
	
	private LoginService getLoginService() throws MalformedURLException 
	{
	    return new YggdrasilLoginService(new URL("https://authserver.mojang.com/authenticate"));
	}
	
	public Session getSession()
	{
		return current_session;
	}
}
