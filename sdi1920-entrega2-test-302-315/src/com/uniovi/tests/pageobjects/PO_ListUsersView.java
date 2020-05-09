package com.uniovi.tests.pageobjects;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_ListUsersView extends PO_NavView {	
	
	static public void fillForm(WebDriver driver, String busqueda) {
		WebElement campoBusqueda = driver.findElement(By.name("busqueda"));
		campoBusqueda.click();
		campoBusqueda.clear();
		campoBusqueda.sendKeys(busqueda);
		//Pulsar el boton de Alta.
		By boton = By.id("searchBtn");
		driver.findElement(boton).click();	
	}
	
}
