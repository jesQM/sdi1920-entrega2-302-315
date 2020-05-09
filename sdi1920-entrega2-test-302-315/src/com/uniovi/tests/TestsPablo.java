package com.uniovi.tests;
//Paquetes Java
import java.util.List;
//Paquetes JUnit 
import org.junit.*;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.assertTrue;
//Paquetes Selenium 
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;

import com.uniovi.tests.util.DatabaseAccess;
//Paquetes Utilidades de Testing Propias
import com.uniovi.tests.util.SeleniumUtils;
//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.*;


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
/*	
	//PR05. Sin hacer /
	@Test
	public void PR05() {
		assertTrue("PR05 sin hacer", false);			
	}
	
	//PR06. Sin hacer /
	@Test
	public void PR06() {
		assertTrue("PR06 sin hacer", false);			
	}
	
	//PR07. Sin hacer /
	@Test
	public void PR07() {
		assertTrue("PR07 sin hacer", false);			
	}	
	
	//PR08. Sin hacer /
	@Test
	public void PR08() {
		assertTrue("PR08 sin hacer", false);			
	}	
	
	//PR09. Sin hacer /
	@Test
	public void PR09() {
		assertTrue("PR09 sin hacer", false);			
	}	
	//PR10. Sin hacer /
	@Test
	public void PR10() {
		assertTrue("PR10 sin hacer", false);			
	}	
	
	//PR11. Sin hacer /
	@Test
	public void PR11() {
		assertTrue("PR11 sin hacer", false);			
	}	
	
	//PR12. Sin hacer /
	@Test
	public void PR12() {
		assertTrue("PR12 sin hacer", false);			
	}	
	
	//PR13. Sin hacer /
	@Test
	public void PR13() {
		assertTrue("PR13 sin hacer", false);			
	}	
	
	//PR14. Sin hacer /
	@Test
	public void PR14() {
		assertTrue("PR14 sin hacer", false);			
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
	
	//PR017. Sin hacer /
	@Test
	public void PR17() {
		assertTrue("PR17 sin hacer", false);			
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
	*/
		
}

