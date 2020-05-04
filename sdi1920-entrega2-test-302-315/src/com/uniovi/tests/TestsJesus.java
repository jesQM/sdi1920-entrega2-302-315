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

import com.uniovi.tests.pageobjects.PO_HomeView;
import com.uniovi.tests.pageobjects.PO_LoginView;
//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.PO_View;
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
		
		PO_LoginView.fillForm(driver, "admin@email.com", "a");
		
		// The logout button is showned
		PO_View.checkElement(driver, "text", " Desconectar");
		//TODO; another test perhaps?
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
		
		PO_LoginView.fillForm(driver, "admin@email.com", "incorrecta");
		
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
		PO_LoginView.fillForm(driver, "admin@email.com", "a");
		
		// Then logout
		PO_HomeView.clickOption(driver, "desconectarse", "class", "form-horizontal");
		PO_View.checkElement(driver, "text", "Identificación de usuario");
	}

	// [Prueba10] Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
	@Test
	public void PR10() {
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, " Desconectar", 2);
	}
	
	//PR15. Sin hacer /
	@Test
	public void PR15() {
		assertTrue("PR15 sin hacer", false);			
	}	
	
	//PR16. Sin hacer /
	@Test
	public void PR16() {
		assertTrue("PR16 sin hacer", false);			
	}	
	
	// [Prueba17] Mostrar el listado de invitaciones de amistad recibidas. Comprobar con un listado que contenga varias invitaciones recibidas.
	@Test
	public void PR17() {
		PO_HomeView.clickOption(driver, "identificarse", "class", "form-horizontal");
		PO_LoginView.fillForm(driver, "admin@email.com", "a");
		
		
		List<WebElement> e = SeleniumUtils.EsperaCargaPaginaxpath(driver, "//*[@id=\"mAmigos\"]/a", 2);
		e.get(0).click();
		PO_HomeView.clickOption(driver, "friends/request", "class", "table-responsive");
		e = driver.findElements(By.xpath("tr"));
		assertTrue(e.size() != 0);
	}	
	
	//PR18. Sin hacer /
	@Test
	public void PR18() {
		assertTrue("PR18 sin hacer", false);			
	}	
	
	//PR19. Sin hacer /
	@Test
	public void PR19() {
		assertTrue("PR19 sin hacer", false);			
	}	
	
	//P20. Sin hacer /
	@Test
	public void PR20() {
		assertTrue("PR20 sin hacer", false);			
	}	
	
	//PR21. Sin hacer /
	@Test
	public void PR21() {
		assertTrue("PR21 sin hacer", false);			
	}	
	
	//PR22. Sin hacer /
	@Test
	public void PR22() {
		assertTrue("PR22 sin hacer", false);			
	}	
	
	//PR23. Sin hacer /
	@Test
	public void PR23() {
		assertTrue("PR23 sin hacer", false);			
	}	
	
	//PR24. Sin hacer /
	@Test
	public void PR24() {
		assertTrue("PR24 sin hacer", false);			
	}	
	//PR25. Sin hacer /
	@Test
	public void PR25() {
		assertTrue("PR25 sin hacer", false);			
	}	
	
	//PR26. Sin hacer /
	@Test
	public void PR26() {
		assertTrue("PR26 sin hacer", false);			
	}	
	
	//PR27. Sin hacer /
	@Test
	public void PR27() {
		assertTrue("PR27 sin hacer", false);			
	}	
	
	//PR029. Sin hacer /
	@Test
	public void PR29() {
		assertTrue("PR29 sin hacer", false);			
	}

	//PR030. Sin hacer /
	@Test
	public void PR30() {
		assertTrue("PR30 sin hacer", false);			
	}
	
	//PR031. Sin hacer /
	@Test
	public void PR31() {
		assertTrue("PR31 sin hacer", false);			
	}
	
		
}

