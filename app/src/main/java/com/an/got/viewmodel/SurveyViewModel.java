package com.an.got.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.an.got.GOTConstants;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.Utils;
import com.google.gson.Gson;

import java.util.Collections;


public class SurveyViewModel extends ViewModel implements GOTConstants {


    private String game;
    private int currentIndex = 0;
    private Long existingScore = 0l;
    private MutableLiveData<Survey> surveyMutableLiveData;

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void incrementCurrentIndex() {
        setCurrentIndex(getCurrentIndex()+1);
    }

    public Long getExistingScore() {
        return existingScore;
    }

    public void setExistingScore(Long existingScore) {
        this.existingScore = existingScore;
    }

    public Question getCurrentQuestion() {
        return getSurveyMutableLiveData().getValue().getQuestions().get(getCurrentIndex());
    }

    public MutableLiveData<Survey> getSurveyMutableLiveData() {
        if(surveyMutableLiveData == null) {
            surveyMutableLiveData = new MutableLiveData<>();
            loadSurvey();
        }
        return surveyMutableLiveData;
    }

    public void loadSurvey() {
        String name = String.format(LOCALE_CACHE_PATH, getGame());
        String responseString = (String) Utils.readObjectFromDisk(name);
        Survey survey = new Gson().fromJson(responseString, Survey.class);
        Collections.shuffle(survey.getQuestions());
        surveyMutableLiveData.setValue(survey);
    }

    public boolean isCorrectAnswer(String text) {
        Question question = getCurrentQuestion();
        for(Answer answer : question.getAnswers()) {
            if(answer.isCorrectAnswer() && text.equalsIgnoreCase(answer.getAnswerDesc())) {
                return true;
            }
        }
        return false;
    }


    public void updateScore(int timeRemaining) {
        updateNumOfTries();
        long score = Utils.getScoreForQuestion(timeRemaining, getCurrentQuestion().getNumTries());
        score = getExistingScore() + score;
        setExistingScore(score);
    }

    public void updateNumOfTries() {
        getCurrentQuestion().updateTries();
    }

    public boolean isGameOver() {
        return (getCurrentQuestion().getNumTries() >= getCurrentQuestion().getMaxTries());
    }
}
