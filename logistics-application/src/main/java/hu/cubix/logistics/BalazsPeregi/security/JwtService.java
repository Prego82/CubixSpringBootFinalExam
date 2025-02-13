package hu.cubix.logistics.BalazsPeregi.security;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

	private static final String AUTH = "auth";
	private static final Algorithm ALGORITHM = Algorithm.HMAC256("secret");

	public String createJwt(UserDetails userDetails) {
		return JWT.create().withSubject(userDetails.getUsername())
				.withArrayClaim(AUTH,
						userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
								.toArray(String[]::new))
				.withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10))).sign(ALGORITHM);
	}

	public UserDetails parseJwt(String jwtToken) {
		DecodedJWT decodedJwt = JWT.require(ALGORITHM).build().verify(jwtToken);
		return new User(decodedJwt.getSubject(), "empty",
				decodedJwt.getClaim(AUTH).asList(String.class).stream().map(SimpleGrantedAuthority::new).toList());
	}

}
