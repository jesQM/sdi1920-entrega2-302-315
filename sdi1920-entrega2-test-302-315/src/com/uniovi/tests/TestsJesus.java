package com.uniovi.tests;
import static org.junit.Assert.assertTrue;

import java.util.List;

//Paquetes JUnit 
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
//Paquetes Selenium 
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.uniovi.tests.pageobjects.PO_Client_LoginView;
import com.uniovi.tests.pageobjects.PO_HomeView;
import com.uniovi.tests.pageobjects.PO_LoginView;
import com.uniovi.tests.pageobjects.PO_NavView;
//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.PO_View;
import com.uniovi.tests.util.DatabaseAccess;
import com.uniovi.tests.util.SeleniumUtils;


//Ordenamos las pruebas por el nombre del mÃ©todo
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class TestsJesus {

	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver024 = "D:\\MiUsuario\\Escritorio\\EclipseStuff\\tercero\\SDI\\Labs\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
	
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
	
	// [Prueba5] Inicio de sesión con datos válidos (usuario estándar).
	@Test
	public void PR05() {
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		PO_LoginView.fillForm(driver, "pedro@email.com", "pedro1");
		
		// The logout button is showned
		PO_View.checkElement(driver, "text", " Desconectar");
		
		// We are on users list view
		PO_View.checkElement(driver, "id", "tableUsers");
	}
	
	// [Prueba6] Inicio de sesión con datos inválidos (usuario estándar, campo email y contraseña vacíos).
	@Test
	public void PR06() {
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		
		PO_LoginView.fillForm(driver, "", "");
		
		// The login button is still there
		PO_View.checkElement(driver, "text", " Identifícate");
		PO_View.checkElement(driver, "text", "Identificación de usuario");
	}
	
	// [Prueba7] Inicio de sesión con datos inválidos (usuario estándar, email existente, pero contraseña incorrecta).
	@Test
	public void PR07() {
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		
		PO_LoginView.fillForm(driver, "pedro@email.com", "incorrecta");
		
		// The login button is still there
		PO_View.checkElement(driver, "text", " Identifícate");
		PO_View.checkElement(driver, "text", "Identificación de usuario");
		
		// The error is showned
		PO_View.checkElement(driver, "class", "alert");
	}
	
	// [Prueba8] Inicio de sesión con datos inválidos (usuario estándar, email no existente y contraseña no vacía).
	@Test
	public void PR08() {
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		
		PO_LoginView.fillForm(driver, "yoNoExisto@sinDominio", "incorrecta");
		
		// The login button is still there
		PO_View.checkElement(driver, "text", " Identifícate");
		PO_View.checkElement(driver, "text", "Identificación de usuario");
		
		// The error is showned
		PO_View.checkElement(driver, "class", "alert");
	}
	
	// [Prueba9] Hacer click en la opción de salir de sesión y comprobar que se redirige a la página de inicio de sesión (Login).
	@Test
	public void PR09() {
		// First, login
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		PO_LoginView.fillForm(driver, "pedro@email.com", "pedro1");
		
		// Then logout
		PO_HomeView.clickOption(driver, "desconectarse", "class", "form-horizontal");
		PO_View.checkElement(driver, "text", "Identificación de usuario");
	}

	// [Prueba10] Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
	@Test
	public void PR10() {
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, " Desconectar", 2);
	}
	
	// [Prueba15] Desde el listado de usuarios de la aplicación, enviar una invitación de amistad a un usuario. Comprobar que la solicitud de amistad aparece en el listado de invitaciones (punto siguiente).
	@Test
	public void PR15() {
		// 1.- Eliminar posible invitación al usuario seleccionado
		String id1 = DatabaseAccess.getUserIdFromEmail("pedro@email.com");
		String id2 = DatabaseAccess.getUserIdFromEmail("ana@email.com");
		DatabaseAccess.removeFriendship(id1, id2);
		
		// Login
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		PO_LoginView.fillForm(driver, "pedro@email.com", "pedro1");
		
		// Seleccionar a la segunda persona
		PO_View.checkElement(driver, "id", "tableUsers");
		List<WebElement> e = SeleniumUtils.EsperaCargaPaginaxpath(driver, "//tr[2]//a", 2);
		e.get(0).click();
		
		// Esperar mensaje
		PO_View.checkElement(driver, "class", "alert-success");
	}	
	
	// [Prueba16] Desde el listado de usuarios de la aplicación, enviar una invitación de amistad a un usuario al
	// que ya le habíamos enviado la invitación previamente. No debería dejarnos enviar la invitación, se podría
	// ocultar el botón de enviar invitación o notificar que ya había sido enviada previamente.
	@Test
	public void PR16() {
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		PO_LoginView.fillForm(driver, "pedro@email.com", "pedro1");
		
		// Seleccionar a la segunda persona
		PO_View.checkElement(driver, "id", "tableUsers");
		List<WebElement> e = SeleniumUtils.EsperaCargaPaginaxpath(driver, "//tr[2]//a", 2);
		e.get(0).click();
		
		// Esperar mensaje de error
		PO_View.checkElement(driver, "class", "alert-warning");
	}	
	
	// [Prueba17] Mostrar el listado de invitaciones de amistad recibidas. Comprobar con un listado que contenga varias invitaciones recibidas.
	@Test
	public void PR17() {
		// 1.- Insert friend requests
		String to = DatabaseAccess.getUserIdFromEmail("pedro@email.com");
		String from1 = DatabaseAccess.getUserIdFromEmail("ana@email.com");
		String from2 = DatabaseAccess.getUserIdFromEmail("susana@email.com");
		// Delete previous in case already friends
		DatabaseAccess.removeFriendship(to, from1);
		DatabaseAccess.removeFriendship(to, from2);
		DatabaseAccess.createFriendship(from1, to, false);
		DatabaseAccess.createFriendship(from2, to, false);
		
		// Login
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		PO_LoginView.fillForm(driver, "pedro@email.com", "pedro1");
		
		// Move to the view
		List<WebElement> e = SeleniumUtils.EsperaCargaPaginaxpath(driver, "//*[@id=\"mAmigos\"]/a", 2);
		e.get(0).click();
		PO_View.checkElement(driver, "id", "mListarPeticiones");
		SeleniumUtils.clickLinkByHref(driver, "/friends/request");
		
		// Count rows
		PO_View.checkElement(driver, "class", "table-responsive");
		e = driver.findElements(By.xpath("//tr"));
		assertTrue(e.size() != 0);
	}	
	
	// [Prueba18] Sobre el listado de invitaciones recibidas. Hacer click en el botón/enlace de una de ellas y comprobar que dicha solicitud desaparece del listado de invitaciones.
	@Test
	public void PR18() {
		// 1.- Insert friend request
		String to = DatabaseAccess.getUserIdFromEmail("pedro@email.com");
		String from = DatabaseAccess.getUserIdFromEmail("ana@email.com");
		// Delete previous in case already friends
		DatabaseAccess.removeFriendship(to, from);
		DatabaseAccess.createFriendship(from, to, false);
		
		// Login
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		PO_LoginView.fillForm(driver, "pedro@email.com", "pedro1");
		
		// Move to the view
		List<WebElement> e = SeleniumUtils.EsperaCargaPaginaxpath(driver, "//*[@id=\"mAmigos\"]/a", 2);
		e.get(0).click();
		PO_View.checkElement(driver, "id", "mListarPeticiones");
		SeleniumUtils.clickLinkByHref(driver, "/friends/request");
		
		// Aceptar invitación
		PO_View.checkElement(driver, "class", "table-responsive");
		SeleniumUtils.clickLinkByHref(driver, "/request/accept/");
		
		// Esperar al mensaje
		PO_View.checkElement(driver, "text", "¡Petición aceptada!");
	}
	
	
	// [Prueba23] Inicio de sesión con datos válidos.
	@Test
	public void PR23() {
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "pedro@email.com", "pedro1");
		
		// Main View Loads
		PO_View.checkElement(driver, "id", "widget-mainView");
	}
	
	// [Prueba24] Inicio de sesión con datos inválidos (usuario no existente en la aplicación).
	@Test
	public void PR24() {
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "noExisto", "niYoTampoco");
		
		// Error PopUps
		PO_View.checkElement(driver, "class", "alert-danger");
	}
	
	// Inicio de sesión con datos inválidos (contraseña errónea).
	@Test
	public void PR24_2() {
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "pedro@email.com", "estoEstáMal");
		
		// Error PopUps
		PO_View.checkElement(driver, "class", "alert-danger");
	}
	
	// [Prueba25] Acceder a la lista de amigos de un usuario, que al menos tenga tres amigos.
	@Test
	public void PR25() {
		// 1.- Tres amigos al usuario
/*		String to = DatabaseAccess.getUserIdFromEmail("pedro@email.com");
		String from1 = DatabaseAccess.getUserIdFromEmail("ana@email.com");
		String from2 = DatabaseAccess.getUserIdFromEmail("susana@email.com");
		String from3 = DatabaseAccess.getUserIdFromEmail("jose@email.com");
		DatabaseAccess.removeFriendship(to, from1);
		DatabaseAccess.removeFriendship(to, from2);
		DatabaseAccess.removeFriendship(to, from3);
		DatabaseAccess.createFriendship(to, from1, true);
		DatabaseAccess.createFriendship(to, from2, true);
		DatabaseAccess.createFriendship(to, from3, true);
*/		
		// Login
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "pedro@email.com", "pedro1");
		
		// Que se muestren 3 amigos
		PO_View.checkElement(driver, "id", "tableFriends");
		PO_View.checkElement(driver, "text", "Ana Fernández"); // Name of one of the friends
		List<WebElement> e = driver.findElements(By.xpath("//*[contains(@id,'tableFriends')]/tr"));
		assertTrue(e.size() >= 3);
	}
	
	// [Prueba26] Acceder a la lista de amigos de un usuario, y realizar un filtrado para encontrar a un amigo
	//	concreto, el nombre a buscar debe coincidir con el de un amigo.
	@Test
	public void PR26() {
		// 1.- Tres amigos al usuario
/*		String to = DatabaseAccess.getUserIdFromEmail("pedro@email.com");
		String from1 = DatabaseAccess.getUserIdFromEmail("ana@email.com");
		String from2 = DatabaseAccess.getUserIdFromEmail("susana@email.com");
		String from3 = DatabaseAccess.getUserIdFromEmail("jose@email.com");
		DatabaseAccess.removeFriendship(to, from1);
		DatabaseAccess.removeFriendship(to, from2);
		DatabaseAccess.removeFriendship(to, from3);
		DatabaseAccess.createFriendship(to, from1, true);
		DatabaseAccess.createFriendship(to, from2, true);
		DatabaseAccess.createFriendship(to, from3, true);
*/		
		// Login
		SeleniumUtils.clickLinkByHref(driver, "cliente");
		PO_Client_LoginView.fillForm(driver, "pedro@email.com", "pedro1");
		
		
		// Search
		PO_View.checkElement(driver, "id", "tableFriends");
		PO_View.checkElement(driver, "text", "Ana Fernández"); // Name of one of the friends
		WebElement search = driver.findElement(By.id("filtro-amigo"));
		search.click();
		search.clear();
		search.sendKeys("Susana");
		
		// Only 1 entry found
		List<WebElement> e = driver.findElements(By.xpath("//*[contains(@id,'tableFriends')]/tr"));
		assertTrue(e.size() == 1);
		PO_View.checkElement(driver, "text", "Susana Martínez"); // Name buscado
	}
	
	// [Prueba27] Acceder a la lista de mensajes de un amigo “chat”, la lista debe contener al menos tres mensajes.
	@Test
	public void PR27() {
		assertTrue("PR27 sin hacer", false);			
	}
}

