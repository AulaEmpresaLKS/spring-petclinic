/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Oliver Drotbohm
 * @author Wick Dynex
 */
@Entity
@Table(name = "owners")
public class Owner extends Person {

	@Column(name = "address")
	@NotBlank
	private String address;

	@Column(name = "city")
	@NotBlank
	private String city;

	@Column(name = "telephone")
	@NotBlank
	@Pattern(regexp = "\\d{10}", message = "Telephone must be a 10-digit number")
	private String telephone;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	@OrderBy("name")
	private final List<Pet> pets = new ArrayList<>();

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public List<Pet> getPets() {
		return this.pets;
	}

	public void addPet(Pet pet) {
		if (pet.isNew()) {
			getPets().add(pet);
		}
	}

	/**
	 * Return the Pet with the given name, or null if none found for this Owner.
	 * @param name to test
	 * @return the Pet with the given name, or null if no such Pet exists for this Owner
	 */
	public Pet getPet(String name) {
		return getPet(name, false);
	}

	/**
	 * Return the Pet with the given id, or null if none found for this Owner.
	 * @param id to test
	 * @return the Pet with the given id, or null if no such Pet exists for this Owner
	 */
	public Pet getPet(Integer id) {
		for (Pet pet : getPets()) {
			if (!pet.isNew()) {
				Integer compId = pet.getId();
				if (compId.equals(id)) {
					return pet;
				}
			}
		}
		return null;
	}

	/**
	 * Return the Pet with the given name, or null if none found for this Owner.
	 * @param name to test
	 * @param ignoreNew whether to ignore new pets (pets that are not saved yet)
	 * @return the Pet with the given name, or null if no such Pet exists for this Owner
	 */
	public Pet getPet(String name, boolean ignoreNew) {
		for (Pet pet : getPets()) {
			String compName = pet.getName();
			if (compName != null && compName.equalsIgnoreCase(name)) {
				if (!ignoreNew || !pet.isNew()) {
					return pet;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.getId())
			.append("new", this.isNew())
			.append("lastName", this.getLastName())
			.append("firstName", this.getFirstName())
			.append("address", this.address)
			.append("city", this.city)
			.append("telephone", this.telephone)
			.toString();
	}

	/**
	 * Adds the given {@link Visit} to the {@link Pet} with the given identifier.
	 * @param petId the identifier of the {@link Pet}, must not be {@literal null}.
	 * @param visit the visit to add, must not be {@literal null}.
	 */
	public void addVisit(Integer petId, Visit visit) {

		Assert.notNull(petId, "Pet identifier must not be null!");
		Assert.notNull(visit, "Visit must not be null!");

		Pet pet = getPet(petId);

		Assert.notNull(pet, "Invalid Pet identifier!");

		pet.addVisit(visit);
	}
	
	// -------------------- Métodos con vulnerabilidades y bugs para Sonar --------------------

	/**
	 * Método que simula una fuga de recurso al no cerrar un FileInputStream.
	 * Este método compila, pero genera un warning en Sonar.
	 */
	public void resourceLeak() throws FileNotFoundException {
		// Se abre un recurso y nunca se cierra
		FileInputStream fis = new FileInputStream("archivo.txt");
		// No se realiza un try-with-resources ni se cierra el stream
		System.out.println("Recurso abierto: " + fis);
	}

	/**
	 * Método que simula una vulnerabilidad de inyección SQL concatenando parámetros.
	 * Aunque compila, es propenso a vulnerabilidades si se ejecuta.
	 */
	public String sqlInjectionVulnerability(String username) {
		// La consulta se construye concatenando el parámetro, lo cual es inseguro
		String query = "SELECT * FROM users WHERE username = '" + username + "'";
		// Se imprime la consulta para simular su ejecución
		System.out.println("Ejecutando consulta: " + query);
		return query;
	}

	/**
	 * Método que declara variables no utilizadas, lo que genera code smells en Sonar.
	 */
	public void unusedVariablesBug() {
		int unusedInt = 42;
		String unusedString = "valor inutil";
		// Variables declaradas pero no utilizadas
		System.out.println("Este método tiene variables no utilizadas.");
	}

	/**
	 * Método con manejo de excepciones pobre: se captura Exception genérica y se ignora.
	 */
	public void emptyCatchBlock() {
		try {
			int division = 10 / 0;
		} catch (Exception e) { // Capturar Exception de forma tan genérica es una mala práctica
			// No se hace nada con la excepción
		}
	}

	/**
	 * Método que retorna una contraseña codificada de forma hardcodeada, lo que es inseguro.
	 */
	public String hardCodedPassword() {
		String password = "1234Abcd!"; // Contraseña hardcodeada
		return password;
	}

	/**
	 * Método que contiene un posible NullPointerException en un bloque inalcanzable,
	 * lo que puede generar alertas en herramientas de análisis estático.
	 */
	public void potentialNPE() {
		if (false) { // Bloque inalcanzable
			Object obj = null;
			// Esta línea genera un NullPointerException si se ejecutase
			System.out.println(obj.toString());
		}
	}
}
