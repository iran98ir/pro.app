package app.vista.one;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {

    private final Context context;

    // محتوای اسلایدها
    private final int[] images = {
        R.drawable.onboarding_1,
        R.drawable.onboarding_2,
        R.drawable.onboarding_3,
        R.drawable.onboarding_4
    };

    private final int[] titles = {
        R.string.onboarding_slide_1_title,
        R.string.onboarding_slide_2_title,
        R.string.onboarding_slide_3_title,
        R.string.onboarding_slide_4_title
    };

    private final int[] descriptions = {
        R.string.onboarding_slide_1_desc,
        R.string.onboarding_slide_2_desc,
        R.string.onboarding_slide_3_desc,
        R.string.onboarding_slide_4_desc
    };

    public OnboardingAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_onboarding, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.slideImage.setImageResource(images[position]);
        holder.slideTitle.setText(titles[position]);
        holder.slideDescription.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    // ============================================
    // ViewHolder
    // ============================================
    static class SlideViewHolder extends RecyclerView.ViewHolder {

        final ImageView slideImage;
        final TextView slideTitle;
        final TextView slideDescription;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            slideImage = itemView.findViewById(R.id.slideImage);
            slideTitle = itemView.findViewById(R.id.slideTitle);
            slideDescription = itemView.findViewById(R.id.slideDescription);
        }
    }
}
