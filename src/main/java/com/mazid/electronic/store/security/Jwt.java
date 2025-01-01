package com.mazid.electronic.store.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class Jwt {

    //validity of token
    public static final long EXPIRATION_TIME = 3600000;


    //secret key
    public static final String SECRET_KEY = "8yV6X3tM3hj6P8E1xT2kR0b3S4dC5fF6gY1h0Z9i2b3c4d5e6f7g8h9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6";

    SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());



    // retrieve username from token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver){
        final Claims claims= getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieving any information from token
    private Claims getAllClaimsFromToken(String token){
        return (Claims) Jwts.parser().verifyWith(secretKey).build().parse(token).getPayload();

//        Key key= Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//        return  Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    //retrieve expiration date from token
    public Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }

    //check if the token has expired
    public Boolean isTokenExpired(String token){
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserDetails userDetails){
//        Key key= Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date()).getTime() + EXPIRATION_TIME))
//                .signWith(key)
//                .compact();

        Map<String,Object> claims = new HashMap<>();
        return doGenerateToken(claims,userDetails.getUsername());
    }

    // while creating the token -
    private  String doGenerateToken(Map<String,Object> claims, String subject){
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt( new Date(System.currentTimeMillis()))
//                .setExpiration(new  Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
//                .compact();

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }


}
