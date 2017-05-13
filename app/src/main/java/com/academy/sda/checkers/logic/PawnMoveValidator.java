package com.academy.sda.checkers.logic;

import android.util.Log;

import com.academy.sda.checkers.model.Board;
import com.academy.sda.checkers.model.Field;
import com.academy.sda.checkers.model.Player;

import static com.academy.sda.checkers.logic.Move.MoveType.*;
import static com.academy.sda.checkers.model.Player.*;

public class PawnMoveValidator {

    private Player currentPlayer;
    private Board board;

    public PawnMoveValidator(Player currentPlayer, Board board) {
        this.currentPlayer = currentPlayer;
        this.board = board;
    }

    public Move.MoveType check(Move move) {
        if (!isValidDirection(move) && !isCapturePossible(move)) {
            logDebug("Invalid Direction");
            return MOVE_ILLEGAL;
        }

        if (move.getFrom().isNeighbour(move.getTo())) {
            logDebug("Target and source are neighbouring fields");
            //Regular moves
            if (board.isFieldEmpty(move.getTo())) {
                logDebug("The target field is empty");
                //Pawn is moved(regular move) / flag informs the controller that the it was a regular move and it was final
                return MOVE_FINAL;
            }
        }
        if (!isCaptureDistance(move)) {
            logDebug("The attempted move is not of a capture distance");
            //Pawn is not moved / flag informs the controller that the it was an illegal move
            return MOVE_ILLEGAL;
        } else {
            if (isCapturePossible(move)) {
                logDebug("Capture is possible");
                if (isAnotherCapturePossibleFrom(move.getTo())) {
                    //Pawn is moved(capturing move) / flag informs the controller that the it was a capturing move and it was not final
                    return CAPTURE_NOT_FINAL;
                } else {
                    //Pawn is moved(capturing move) / flag informs the controller that the it was a capturing move and it was final
                    return CAPTURE_FINAL;
                }
            }
        }
        logDebug("No legal moves!");
        return MOVE_ILLEGAL;
    }

    private boolean isValidDirection(Move move) {
        if (currentPlayer == PLAYER_A) {
            return move.getFrom().getRow() < move.getTo().getRow();
        } else {
            return move.getFrom().getRow() > move.getTo().getRow();
        }
    }

    private boolean isCaptureDistance(Move move) {
        return Math.abs(move.getFrom().getRow() - move.getTo().getRow()) == 2 &&
                Math.abs(move.getFrom().getColumn() - move.getTo().getColumn()) == 2;
    }

    private boolean isEnemyInBetween(Move move) {
        return board.getPawn(board.getFieldInBetween(move)).getPlayer() ==
                Player.getEnemy(currentPlayer);
    }

    private boolean isAnotherCapturePossibleFrom(Field field) {
        return isCapturePossible(new Move(field,
                new Field(field.getRow() + 2, field.getColumn() + 2))) ||
                isCapturePossible(new Move(field,
                        new Field(field.getRow() - 2, field.getColumn() - 2))) ||
                isCapturePossible(new Move(field,
                        new Field(field.getRow() + 2, field.getColumn() - 2))) ||
                isCapturePossible(new Move(field,
                        new Field(field.getRow() - 2, field.getColumn() + 2)));
    }

    private boolean isCapturePossible(Move move) {
        boolean capturePossible;
        try {
            capturePossible = board.isFieldEmpty(move.getTo()) && isEnemyInBetween(move);
        } catch (ArrayIndexOutOfBoundsException e) {
            logDebug("Is capture from " + move.getFrom() + " to " + move.getTo() + " possible? " + false);
            return false;
        }
        logDebug("Is capture from " + move.getFrom() + " to " + move.getTo() + " possible? " + capturePossible);
        return capturePossible;

    }

   private void logDebug(String msg) {
        Log.e(this.getClass().getSimpleName(), msg);
    }


}
