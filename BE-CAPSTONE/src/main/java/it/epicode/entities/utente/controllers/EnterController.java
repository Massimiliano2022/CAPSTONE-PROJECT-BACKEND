package it.epicode.entities.utente.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.epicode.entities.utente.Utente;
import it.epicode.entities.utente.payloads.LoginPayload;
import it.epicode.entities.utente.payloads.TokenPayload;
import it.epicode.entities.utente.payloads.UtentePayload;
import it.epicode.entities.utente.services.UtenteService;
import it.epicode.exceptions.UnauthorizedException;
import it.epicode.security.JwtTools;

@RestController
@RequestMapping("/auth")
public class EnterController {

	@Autowired
	UtenteService utenteservice;

	@Autowired
	private PasswordEncoder bcrypt;

	@PostMapping("/login")
	public ResponseEntity<TokenPayload> login(@RequestBody @Validated LoginPayload body) {

		Utente utente = utenteservice.findByEmail(body.getEmail());

		String plainPW = body.getPassword();
		String hashedPW = utente.getPassword();

		if (!bcrypt.matches(plainPW, hashedPW))
			throw new UnauthorizedException("Credenziali non valide");

		String token = JwtTools.createToken(utente);

		return new ResponseEntity<>(new TokenPayload(token, utente), HttpStatus.OK);
	}

	@PostMapping("/register")
	public Utente register(@RequestBody @Validated UtentePayload payload) {
		payload.setPassword(bcrypt.encode(payload.getPassword()));
		return utenteservice.create(payload);
	}
}
