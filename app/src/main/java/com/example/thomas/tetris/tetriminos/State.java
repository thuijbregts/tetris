package com.example.thomas.tetris.tetriminos;

public class State {

    private Mino[] minos;
    private State previousState;
    private State nextState;

    public State(Mino[] minos){
        this.minos = minos;
    }

    public Mino[] getMinos() {
        return minos;
    }

    public State getPreviousState() {
        return previousState;
    }

    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    public State getNextState() {
        return nextState;
    }

    public void setNextState(State nextState) {
        this.nextState = nextState;
    }
}
