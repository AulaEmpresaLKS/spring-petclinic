package org.springframework.samples.petclinic.owner;

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
 * (Autores omitidos para mayor claridad)
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
	 * Método dummy para forzar que SonarQube detecte la siguiente ISSUE: "Change this
	 * code to not construct SQL queries directly from user-controlled data".
	 *
	 * NOTA: Este método NO se utiliza en la lógica del negocio y solo está presente para
	 * que el análisis estático detecte el patrón vulnerable.
	 * @param userInput entrada controlada por el usuario
	 * @return Consulta SQL construida de forma insegura
	 */
	public String buildVulnerableQuery(String userInput) {
		String vulnerableQuery = "SELECT * FROM Users WHERE email = '" + userInput + "'";
		return vulnerableQuery;
	}

	// ------------------------------------------------------------------------
	// MÉTODOS ADICIONALES PARA FORZAR DETECCIÓN DE ISSUES EN SONARQUBE
	// ------------------------------------------------------------------------

	/**
	 * Método dummy para forzar un code smell: variable no utilizada.
	 */
	public void unusedMethod() {
		int unusedVariable = 42;
		// La variable 'unusedVariable' se declara pero nunca se utiliza.
	}

	/**
	 * Método dummy para forzar un posible bug: riesgo de NullPointerException. Este
	 * método asume que el nombre de la mascota puede ser nulo.
	 */
	public void processPetNames() {
		for (Pet pet : getPets()) {
			// Potencial NullPointerException si pet.getName() es null.
			if (pet.getName().equals("Fluffy")) {
				System.out.println("Se encontró la mascota Fluffy");
			}
		}
	}

	/**
	 * Método dummy para forzar un code smell relacionado con el manejo de excepciones. Se
	 * utiliza un bloque catch vacío para capturar ArithmeticException.
	 */
	public void riskyOperation() {
		try {
			// Código que lanza una ArithmeticException (división por cero)
			int result = 10 / 0;
		}
		catch (ArithmeticException e) {
			// Bloque catch vacío: se ignora la excepción.
		}
	}

	/**
	 * Método dummy adicional para forzar un bug y vulnerabilidad: Construcción insegura
	 * de consulta SQL mediante concatenación de datos controlados por el usuario.
	 * @param userInput entrada controlada por el usuario
	 * @return Consulta SQL construida de forma insegura
	 */
	public String buildAnotherVulnerableQuery(String userInput) {
		return "SELECT * FROM Products WHERE name = '" + userInput + "'";
	}

}
