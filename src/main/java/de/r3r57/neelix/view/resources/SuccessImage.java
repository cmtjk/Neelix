package de.r3r57.neelix.view.resources;

import java.nio.file.Paths;

import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;
import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class SuccessImage extends ImageView {

    private FadeTransition fadeTransition;

    public SuccessImage() {

	createSuccessImage();

    }

    public void play() {
	if (fadeTransition != null) {
	    this.setVisible(true);
	    fadeTransition.stop();
	    fadeTransition.play();
	}
    }

    private void createSuccessImage() {

	this.setFitWidth(25);
	this.setFitHeight(25);
	this.setPreserveRatio(true);
	this.setSmooth(true);
	this.setCache(true);
	this.setImage(new Image(Paths.get("./config/success.png").toAbsolutePath().toUri().toString()));

	this.setVisible(false);

    }

    public void setTransition(Double seconds) throws SystemException {
	if (seconds != null) {
	    fadeTransition = new FadeTransition(Duration.seconds(seconds), this);
	    fadeTransition.setFromValue(1);
	    fadeTransition.setToValue(0);
	    fadeTransition.setAutoReverse(true);
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "constructor").set("arg0", "seconds=" + seconds);
	}
    }

}
