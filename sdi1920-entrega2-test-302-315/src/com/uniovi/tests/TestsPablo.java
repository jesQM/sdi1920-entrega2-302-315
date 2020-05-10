package com.uniovi.tests;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

//Paquetes JUnit 
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.uniovi.tests.pageobjects.PO_Client_ChatView;
import com.uniovi.tests.pageobjects.PO_Client_LoginView;
import com.uniovi.tests.pageobjects.PO_HomeView;
import com.uniovi.tests.pageobjects.PO_ListUsersView;
import com.uniovi.tests.pageobjects.PO_LoginView;
import com.uniovi.tests.pageobjects.PO_NavView;
import com.uniovi.tests.pageobjects.PO_RegisterView;
import com.uniovi.tests.pageobjects.PO_View;
import com.uniovi.tests.util.DatabaseAccess;
//Paquetes Utilidades de Testing Propias
import com.uniovi.tests.util.SeleniumUtils;


//Ordenamos las pruebas por el nombre del mÃ©todo
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class TestsPablo {

	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver024 = "D:\\Users\\kendo\\Downloads\\PL-SDI-Sesion5\\geckodriver024win64.exe";
	
	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024); 
	static String URL = "http://localhost:8081";


	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}


	@Before
	public void setUp(){
		driver.navigate().to(URL);
	}
	@After
	public void tearDown(){
		driver.manage().deleteAllCookies();
	}
	@BeforeClass 
	static public void begin() {
		//COnfiguramos las pruebas.
		//Fijamos el timeout en cada opciÃ³n de carga de una vista. 2 segundos.
		PO_View.setTimeout(3);

	}
	@AfterClass
	static public void end() {
		//Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}
	
	//PR01. Registro de Usuario con datos válidos. /
	@Test
	public void PR01() {
		PO_HomeView.clickOption(driver, "registrarse", "class", "btn btn-primary");
		PO_RegisterView.fillForm(driver, "Valido", "Validin", "valido@email.com", "123456", "123456");
		PO_LoginView.checkElement(driver, "class", "btn btn-primary");
		
		DatabaseAccess.removeUser("valido@email.com");
		DatabaseAccess.closeDatabase();
	}

	//PR02. Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos). /
	@Test
	public void PR02() {
		PO_HomeView.clickOption(driver, "registrarse", "class", "btn btn-primary");
		PO_RegisterView.fillForm(driver, "", "", "", "123456", "123456");
		PO_RegisterView.checkElement(driver, "class", "btn btn-primary");
	}

	//PR03. Registro de Usuario con datos inválidos (repetición de contraseña inválida). /
	@Test
	public void PR03() {
		PO_HomeView.clickOption(driver, "registrarse", "class", "btn btn-primary");
		PO_RegisterView.fillForm(driver, "No Valido", "No Validin", "valido@email.com", "123456", "12");
		SeleniumUtils.textoPresentePagina(driver, "Las contraseñas no coinciden");			
	}
	
	//PR04. Registro de Usuario con datos inválidos (email existente). /
	@Test
	public void PR04() {
		PO_HomeView.clickOption(driver, "registrarse", "class", "btn btn-primary");
		PO_RegisterView.fillForm(driver, "No Valido", "No Validin", "pedro@email.com", "123456", "123456");
		SeleniumUtils.textoPresentePagina(driver, "Ya existe un usuario con el email: pedro@email.com");	
	}

	//PR11. Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el sistema. /
	@Test
	public void PR11() {
		SeleniumUtils.login(driver, "ana@email.com", "ana1");
		PO_View.checkElement(driver, "id", "tableUsers");
		
		// contamos users de la tabla
		int usersCount = 0;
		boolean isNextPage = false;
		int i = 2;
		do {
			
			List<WebElement> users = driver.findElements(By.xpath("//*[@id=\"tableUsers\"]/tbody/tr"));
			usersCount += users.size();
			
			List<WebElement> elementos = driver.findElements(By.xpath("//*[@id=\"pi-"+i+"\"]/a"));
			if(elementos.size() == 1) {
				i++;
				isNextPage = true;
				elementos.get(0).click();
			} else {
				isNextPage = false;
			}
			
		} while (isNextPage); // nextPageOfList
		
		assertEquals(usersCount, DatabaseAccess.getNumberOfUsers());
	}	
	
	//PR12. Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
	// 		corresponde con el listado usuarios existentes en el sistema.
	@Test
	public void PR12() {
		SeleniumUtils.login(driver, "ana@email.com", "ana1");
		PO_View.checkElement(driver, "id", "tableUsers");
		
		// buscamos nada
		PO_ListUsersView.fillForm(driver, "");
		
		// contamos users de la tabla
		int usersCount = 0;
		boolean isNextPage = false;
		int i = 2;
		do {
			
			List<WebElement> users = driver.findElements(By.xpath("//*[@id=\"tableUsers\"]/tbody/tr"));
			usersCount += users.size();
			
			List<WebElement> elementos = driver.findElements(By.xpath("//*[@id=\"pi-"+i+"\"]/a"));
			if(elementos.size() == 1) {
				i++;
				isNextPage = true;
				elementos.get(0).click();
			} else {
				isNextPage = false;
			}
			
		} while (isNextPage); // nextPageOfList
		
		assertEquals(DatabaseAccess.getNumberOfUsers(), usersCount);
	}	
	
	//PR13. Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
	//		muestra la página que corresponde, con la lista de usuarios vacía
	@Test
	public void PR13() {
		SeleniumUtils.login(driver, "ana@email.com", "ana1");
		PO_View.checkElement(driver, "id", "tableUsers");
		
		// buscamos algo no existente
		PO_ListUsersView.fillForm(driver, UUID.randomUUID().toString());
		
		// contamos users de la tabla
		int usersCount = 0;
		boolean isNextPage = false;
		int i = 2;
		do {
			
			List<WebElement> users = driver.findElements(By.xpath("//*[@id=\"tableUsers\"]/tbody/tr"));
			usersCount += users.size();
			
			List<WebElement> elementos = driver.findElements(By.xpath("//*[@id=\"pi-"+i+"\"]/a"));
			if(elementos.size() == 1) {
				i++;
				isNextPage = true;
				elementos.get(0).click();
			} else {
				isNextPage = false;
			}
			
		} while (isNextPage); // nextPageOfList
		
		assertEquals(0, usersCount);
	}	
	
	//PR14. Hacer una búsqueda con un texto específico y comprobar que se muestra la página que
	// 		corresponde, con la lista de usuarios en los que el texto especificados sea parte de su nombre, apellidos o
	// 		de su email
	@Test
	public void PR14() {
		SeleniumUtils.login(driver, "ana@email.com", "ana1");
		PO_View.checkElement(driver, "id", "tableUsers");
		
		// buscamos algo existente
		PO_ListUsersView.fillForm(driver, "jose manuel");
		
		// contamos users de la tabla
		int usersCount = 0;
		boolean isNextPage = false;
		int i = 2;
		do {
			
			List<WebElement> users = driver.findElements(By.xpath("//*[@id=\"tableUsers\"]/tbody/tr"));
			usersCount += users.size();
			
			List<WebElement> elementos = driver.findElements(By.xpath("//*[@id=\"pi-"+i+"\"]/a"));
			if(elementos.size() == 1) {
				i++;
				isNextPage = true;
				elementos.get(0).click();
			} else {
				isNextPage = false;
			}
			
		} while (isNextPage); // nextPageOfList
		
		assertEquals(1, usersCount);
		SeleniumUtils.textoPresentePagina(driver, "Jose");	
	}	
	
	//PR19. Mostrar el listado de amigos de un usuario. 
	//		Comprobar que el listado contiene los amigos que deben ser.
	@Test
	public void PR19() {
		SeleniumUtils.login(driver, "dummy1@email.com", "dummy1");
		
		// listar amigos
		List<WebElement> e = SeleniumUtils.EsperaCargaPaginaxpath(driver, "//*[@id=\"mAmigos\"]/a", 2);
		e.get(0).click();
		PO_NavView.clickOption(driver, "friends/list", "class", "table-responsive");
		
		int usersCount = 0;
		boolean isNextPage = false;
		int i = 2;
		do {
			
			List<WebElement> users = driver.findElements(By.xpath("//*[@id=\"tableUsers\"]/tbody/tr"));
			usersCount += users.size();
			
			List<WebElement> elementos = driver.findElements(By.xpath("//*[@id=\"pi-"+i+"\"]/a"));
			if(elementos.size() == 1) {
				i++;
				isNextPage = true;
				elementos.get(0).click();
			} else {
				isNextPage = false;
			}
			
		} while (isNextPage); // nextPageOfList
		
		// solo tiene un amigo --> dummy2
		assertEquals(DatabaseAccess.getNumberOfFriendsOfUser("dummy1@email.com"), usersCount);
		DatabaseAccess.closeDatabase();
	}	
	
	//P20.  Intentar acceder sin estar autenticado a la opción de listado de usuarios. 
	//		Se deberá volver al formulario de login.
	@Test
	public void PR20() {
		driver.navigate().to(URL + "/usuarios");
		PO_LoginView.checkElement(driver, "class", "btn btn-primary");
	}	
	
	//PR21. Intentar acceder sin estar autenticado a la opción de listado de invitaciones de amistad recibida
	// 		de un usuario estándar. Se deberá volver al formulario de login
	@Test
	public void PR21() {
		driver.navigate().to(URL + "/friends/request");		
		PO_LoginView.checkElement(driver, "class", "btn btn-primary");
	}
	
	
	
	
	/*-----------------------------------------------------------------------------------------
	 * 
	 * Este test no puede realizarse en nuestro caso, dado que los amigos se muestran
	 * en función del usuario que se encuentra en sesión, y por tanto no es algo que pueda
	 * vulnerarse a través de una petición de otro tipo.

	//PR22. Intentar acceder estando autenticado como usuario standard a la lista de amigos de otro
	// 		usuario. Se deberá mostrar un mensaje de acción indebida.
	@Test
	public void PR22() {
		SeleniumUtils.login(driver, "ana@email.com", "ana1");		
	}	
	
	*-----------------------------------------------------------------------------------------
	*/
	
	
	//PR28. Acceder a la lista de mensajes de un amigo “chat” y crear un nuevo mensaje,
	//   	validar que el mensaje aparece en la lista de mensajes.
	@Test
	public void PR28() {
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "dummy1@email.com", "dummy1");
		PO_View.checkElement(driver, "id", "tableFriends");
		
		List<WebElement> users = SeleniumUtils.EsperaCargaPaginaxpath(driver, " /html/body/div[2]/div/div/div[1]/div/table/tbody/tr/td[1]", 3);
		users.get(0).click();
		
		String text = "Esto es un mensaje.";
		PO_Client_ChatView.fillForm(driver, text);
		
		SeleniumUtils.esperarSegundos(driver, 2);
		SeleniumUtils.textoPresentePagina(driver, text);
	}
	
	//PR29. Identificarse en la aplicación y enviar un mensaje a un amigo, validar que el mensaje enviado
	//		aparece en el chat. Identificarse después con el usuario que recibido el mensaje y validar que tiene un
	//		mensaje sin leer, entrar en el chat y comprobar que el mensaje pasa a tener el estado leído.
	@Test
	public void PR29() {
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "dummy1@email.com", "dummy1");
		PO_View.checkElement(driver, "id", "tableFriends");
		
		List<WebElement> users = SeleniumUtils.EsperaCargaPaginaxpath(driver, " /html/body/div[2]/div/div/div[1]/div/table/tbody/tr/td[1]", 3);
		users.get(0).click();
		
		String text1 = "Mensaje para Test29";
		
		PO_Client_ChatView.fillForm(driver, text1);
		SeleniumUtils.esperarSegundos(driver, 2);
		SeleniumUtils.textoPresentePagina(driver, text1);
		
		SeleniumUtils.clickLinkByHref(driver, "home");
		
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "dummy2@email.com", "dummy2");
		PO_View.checkElement(driver, "id", "tableFriends");
		
		List<WebElement> users2 = SeleniumUtils.EsperaCargaPaginaxpath(driver, "/html/body/div[2]/div/div/div[1]/div/table/tbody/tr/td[1]", 1);
		users2.get(0).click();
		assertEquals(DatabaseAccess.getNumberOfNonReadedMessages("dummy1@email.com"), 0);
	}
	
	//PR30. Identificarse en la aplicación y enviar tres mensajes a un amigo, validar que los mensajes
	// 		enviados aparecen en el chat. Identificarse después con el usuario que recibido el mensaje y validar que el
	// 		número de mensajes sin leer aparece en la propia lista de amigos.
	@Test
	public void PR30() {
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "dummy1@email.com", "dummy1");
		PO_View.checkElement(driver, "id", "tableFriends");
		
		List<WebElement> users = SeleniumUtils.EsperaCargaPaginaxpath(driver, " /html/body/div[2]/div/div/div[1]/div/table/tbody/tr/td[1]", 3);
		users.get(0).click();
		
		String text1 = "Mensaje 1.";
		String text2 = "Mensaje 2.";
		String text3 = "Mensaje 3.";
		
		PO_Client_ChatView.fillForm(driver, text1);
		SeleniumUtils.esperarSegundos(driver, 2);
		
		PO_Client_ChatView.fillForm(driver, text2);
		SeleniumUtils.esperarSegundos(driver, 2);
		
		PO_Client_ChatView.fillForm(driver, text3);
		SeleniumUtils.esperarSegundos(driver, 2);
		
		SeleniumUtils.textoPresentePagina(driver, text1);
		SeleniumUtils.textoPresentePagina(driver, text2);
		SeleniumUtils.textoPresentePagina(driver, text3);
		
		SeleniumUtils.clickLinkByHref(driver, "home");
		
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "dummy2@email.com", "dummy2");
		PO_View.checkElement(driver, "id", "tableFriends");
		
		SeleniumUtils.EsperaCargaPaginaxpath(driver, " /html/body/div[2]/div/div/div[1]/div/table/tbody/tr/td[1]", 3);
		SeleniumUtils.textoPresentePagina(driver, String.valueOf(DatabaseAccess.getNumberOfNonReadedMessages("dummy2@email.com")));
		
	}
		
}

