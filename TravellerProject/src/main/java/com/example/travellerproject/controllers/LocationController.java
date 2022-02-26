package com.example.travellerproject.controllers;
import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageComponents;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import com.example.travellerproject.exceptions.NotFoundException;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.services.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@PropertySource(value={"classpath:application.properties"})
public class LocationController {

    @Value("${open.cage.apikey}")
    String appKey;
    @Autowired
    private Validator validator;
    @Autowired
    private SessionValidator sessionValidator;

    @GetMapping(value = "/post/{postId}/location")
    public JOpenCageComponents showLocation(@PathVariable long postId, HttpSession session){
        sessionValidator.isUserLogged(session);
        Post post =  validator.validatePostAndGet(postId);
        double latitude= Double.parseDouble(post.getLatitude());
        double longitude= Double.parseDouble(post.getLongitude());
        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(appKey);
        JOpenCageReverseRequest request = new JOpenCageReverseRequest(latitude, longitude);
        request.setNoAnnotations(true);
        JOpenCageResponse responseJ = jOpenCageGeocoder.reverse(request);
        return responseJ.getResults().stream().findAny().orElseThrow(() -> new NotFoundException("Location is compromised.")).getComponents();

    }
}
