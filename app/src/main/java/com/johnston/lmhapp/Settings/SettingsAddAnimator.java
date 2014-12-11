package com.johnston.lmhapp.Settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.johnston.lmhapp.R;

import java.sql.Array;
import java.util.ArrayList;

/**
 * Created by Johnston on 11/12/2014.
 */
public class SettingsAddAnimator extends RecyclerView.ItemAnimator {


    private ArrayList<RecyclerView.ViewHolder> currentAdds = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> currentRemovals = new ArrayList<>();
    private ArrayList<MoveInfo> currentMoves = new ArrayList<>();

    private ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList =     new ArrayList<ArrayList<RecyclerView.ViewHolder>>();
    private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<ArrayList<MoveInfo>>();

    private ArrayList<RecyclerView.ViewHolder> pendingAdds = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> pendingRemovals = new ArrayList<>();
    private ArrayList<MoveInfo> pendingMoves = new ArrayList<>();

    private static class MoveInfo {
        public RecyclerView.ViewHolder holder;
        public int fromX, fromY, toX, toY;

        private MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }


    @Override
    public void runPendingAnimations() {
        Boolean pendingRemovalsBoolean = !pendingRemovals.isEmpty();
        Boolean pendingMovesBoolean = !pendingMoves.isEmpty();
//        Removals First
        for(int i=0;i<pendingRemovals.size();i++){
            dismissPosition(pendingRemovals.get(i));
            currentRemovals.add(pendingRemovals.get(i));
        }
        pendingRemovals.clear();

//        Moves Next

        final ArrayList<MoveInfo> moves = new ArrayList<MoveInfo>();
        moves.addAll(pendingMoves);
        pendingMoves.clear();
        for(int i=0;i<moves.size();i++){
                currentMoves.add(moves.get(i));
            }

            Runnable mover = new Runnable() {
                @Override
                public void run() {
                    for (int i=0;i<moves.size();i++) {
                        moveItems(moves.get(i));
                    }
                }
            };

            if (pendingRemovalsBoolean) {
                           Handler handler = new Handler();
                           handler.postDelayed(mover,500);
            } else {
                mover.run();
            }


//        Finish with additions.

        final ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>();
        additions.addAll(pendingAdds);
        pendingAdds.clear();
        for(int i=0;i<additions.size();i++){
            currentAdds.add(additions.get(i));
        }
        Runnable adder = new Runnable(){
            @Override
        public void run(){
                for(int i=0;i<additions.size();i++){
                    performAdd(additions.get(i));
                }
            }
        };
        long delay=0;
        if(pendingRemovalsBoolean){
            delay = delay+500;
        }
        if(pendingMovesBoolean){
            delay = delay+250;
        }
        Handler handler = new Handler();
        handler.postDelayed(adder,delay);
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        endAnimation(holder);
        pendingRemovals.add(holder);
        return true;
    }

    public void dismissPosition(final RecyclerView.ViewHolder viewHolder){

        final View dismissView = viewHolder.itemView;
        dismissView.animate()
                .translationX(dismissView.getWidth())
                .alpha(1)
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation){
                        dispatchRemoveStarting(viewHolder);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        currentRemovals.remove(viewHolder);
                        dispatchRemoveFinished(viewHolder);
                        dispatchFinishedWhenDone();
                    }
                });
    }




    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        endAnimation(holder);
        holder.itemView.setAlpha(0);
        pendingAdds.add(holder);
        return true;
    }

    public void performAdd(final RecyclerView.ViewHolder holder){
        holder.itemView.setTranslationX(holder.itemView.getWidth());
        holder.itemView.setAlpha(1);
        View addView = holder.itemView;
        addView.animate()
                .translationX(0)
                .alpha(1)
                .setDuration(250)
//                If I remove this listener it doesn't work. This is problem a bad sign...
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        currentAdds.remove(holder);
                        dispatchAddFinished(holder);
                        dispatchFinishedWhenDone();
                    }
                });
        ;

    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        endAnimation(holder);
        final View view = holder.itemView;
        fromX += ViewCompat.getTranslationX(holder.itemView);
        fromY += ViewCompat.getTranslationY(holder.itemView);
        endAnimation(holder);
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaX != 0) {
            view.setTranslationX(-deltaX);
        }
        if (deltaY != 0) {
            view.setTranslationY(-deltaY);
        }
        pendingMoves.add(new MoveInfo(holder,fromX,fromY,toX,toY));
        return true;
    }

    public void moveItems(final MoveInfo moveInfo){
        final View view = moveInfo.holder.itemView;
        view.animate()
                .translationY(0)
                .translationX(0)
                .alpha(1)
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation){
                        dispatchMoveStarting(moveInfo.holder);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        currentMoves.remove(moveInfo);
                        dispatchMoveFinished(moveInfo.holder);
                        dispatchFinishedWhenDone();
                    }
                });
        ;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        item.itemView.clearAnimation();

        for(int i=0;i<pendingAdds.size();i++){
            if(pendingAdds.get(i).equals(item)){
                dispatchAddFinished(item);
                item.itemView.setAlpha(1);
                pendingAdds.remove(i);
                return;
            }
        }

        for(int i=0;i<pendingMoves.size();i++){
            if(pendingMoves.get(i).holder.equals(item)){
                dispatchAddFinished(item);
                item.itemView.setTranslationX(0);
                item.itemView.setTranslationY(0);
                pendingMoves.remove(i);
                return;
            }
        }
        for(int i=0;i<pendingRemovals.size();i++){
            if(pendingRemovals.get(i).equals(item)){
                dispatchRemoveFinished(item);
//                It is being removed so I don't care how it looks?
                pendingRemovals.remove(i);
                return;
            }
        }

        for(int i=0;i<currentMoves.size();i++){
            if(currentMoves.get(i).holder.equals(item)){
                dispatchAddFinished(item);
                item.itemView.clearAnimation();
                item.itemView.setTranslationX(0);
                item.itemView.setTranslationY(0);
                currentMoves.remove(i);
                return;
            }
        }

        for(int i=0;i<currentAdds.size();i++){
            if(currentAdds.get(i).equals(item)){
                item.itemView.clearAnimation();

                SettingsRecyclerAdapter.EntryHolder entryHolder = (SettingsRecyclerAdapter.EntryHolder) item;
                item.itemView.setTranslationX(0);
                item.itemView.setAlpha(1); // Unnecessary I think.

                dispatchAddFinished(item);

                currentAdds.remove(i);
                return;
            }
        }
        for(int i=0;i<currentRemovals.size();i++){
            if(currentRemovals.get(i).equals(item)){
                item.itemView.clearAnimation();
                currentRemovals.remove(i);
                dispatchRemoveFinished(item);
                return;
            }
        }
    }

    public void stopAnimation(RecyclerView.ViewHolder item){
        for(int i=0;i<pendingAdds.size();i++){
            if(pendingAdds.get(i).equals(item)){
                dispatchAddFinished(item);
                item.itemView.setAlpha(1);
                pendingAdds.remove(i);
                return;
            }
        }

        for(int i=0;i<pendingMoves.size();i++){
            if(pendingMoves.get(i).holder.equals(item)){
                dispatchAddFinished(item);
                pendingMoves.remove(i);
                return;
            }
        }

        for(int i=0;i<pendingRemovals.size();i++){
            if(pendingRemovals.get(i).equals(item)){
                dispatchRemoveFinished(item);
//                It is being removed so I don't care how it looks?
                pendingRemovals.remove(i);
                return;
            }
        }
        for(int i=0;i<currentAdds.size();i++){
            if(currentAdds.get(i).equals(item)){
                item.itemView.clearAnimation();
                dispatchAddFinished(item);
                currentAdds.remove(i);
                return;
            }
        }

        for(int i=0;i<currentMoves.size();i++){
            if(currentMoves.get(i).holder.equals(item)){
                dispatchAddFinished(item);
                item.itemView.clearAnimation();
                currentMoves.remove(i);
                return;
            }
        }

        for(int i=0;i<currentRemovals.size();i++){
            if(currentRemovals.get(i).equals(item)){
                item.itemView.clearAnimation();
                currentRemovals.remove(i);
                dispatchRemoveFinished(item);
                return;
            }
        }
    }
    @Override
    public void endAnimations() {
        for(int i=0;i<pendingAdds.size();i++){
            RecyclerView.ViewHolder item = pendingAdds.get(i);
            dispatchAddFinished(item);
//                I think it will already have the right settings.
            pendingAdds.remove(i);
        }

        for(int i=0;i<pendingMoves.size();i++) {
            RecyclerView.ViewHolder item = pendingMoves.get(i).holder;
            dispatchAddFinished(item);
            item.itemView.setTranslationX(0);
            item.itemView.setTranslationY(0);
            pendingMoves.remove(i);
        }
        for(int i=0;i<pendingRemovals.size();i++){
            RecyclerView.ViewHolder item = pendingRemovals.get(i);
            dispatchRemoveFinished(item);
//                I think it will already have the right settings.
            pendingRemovals.remove(i);
        }
        for(int i=0;i<currentAdds.size();i++){
            RecyclerView.ViewHolder item = currentAdds.get(i);
            item.itemView.clearAnimation();
            item.itemView.setTranslationX(0);
            item.itemView.setAlpha(1); //Unnecessary?
            dispatchAddFinished(item);

            currentAdds.remove(i);
        }
        for(int i=0;i<currentMoves.size();i++) {
            RecyclerView.ViewHolder item = currentMoves.get(i).holder;
            dispatchAddFinished(item);
            item.itemView.setTranslationX(0);
            item.itemView.setTranslationY(0);
            currentMoves.remove(i);
        }

        for(int i=0;i<currentRemovals.size();i++){
            RecyclerView.ViewHolder item = currentRemovals.get(i);
            item.itemView.clearAnimation();
            currentRemovals.remove(i);
            dispatchRemoveFinished(item);
        }

        dispatchAnimationsFinished();
    }

    @Override
    public boolean isRunning() {
        return !currentAdds.isEmpty()||!currentRemovals.isEmpty()||!currentMoves.isEmpty();
    }

    private void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }
}
