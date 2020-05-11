package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.uniovi.tests.util.SeleniumUtils;

public class PO_Client_LoginView extends PO_NavView {

	static public void fillForm(WebDriver driver, String dnip, String passwordp) {
		SeleniumUtils.esperarSegundos(driver, 2);
		WebElement dni = driver.findElement(By.id("email"));
		dni.click();
		dni.clear();
		dni.sendKeys(dnip);
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys(passwordp);
		By boton = By.id("boton-login");
		driver.findElement(boton).click();	
	}

}
