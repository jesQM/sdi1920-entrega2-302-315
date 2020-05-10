package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_Client_ChatView extends PO_NavView {

	static public void fillForm(WebDriver driver, String dnip) {
		WebElement dni = driver.findElement(By.id("textArea-mensaje"));
		dni.click();
		dni.clear();
		dni.sendKeys(dnip);
		By boton = By.id("btn-crear-mensaje");
		driver.findElement(boton).click();	
	}

}
