package co.resume.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.craft.resumebuilder.resume.AchievementsActivity;
import com.craft.resumebuilder.resume.DeclarationActivity;
import com.craft.resumebuilder.resume.EducationalDetailsActivity;
import com.craft.resumebuilder.resume.InterestsActivity;
import com.craft.resumebuilder.resume.LanguagesActivity;
import com.craft.resumebuilder.resume.ObjectiveActivity;
import com.craft.resumebuilder.resume.PersonalDetailsActivity;
import com.craft.resumebuilder.resume.ProjectsActivity;
import com.craft.resumebuilder.resume.ResumeTemplateActivity;
import com.craft.resumebuilder.resume.SkillsActivity;
import com.craft.resumebuilder.resume.WorkExperienceActivity;

public class ViewPagerAdapter
        extends FragmentPagerAdapter {

    public ViewPagerAdapter(
            @NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new ObjectiveActivity();
        else if (position == 1)
            fragment = new PersonalDetailsActivity();
        else if (position == 2)
            fragment = new ResumeTemplateActivity();
        else if (position == 3)
            fragment = new EducationalDetailsActivity();
        else if (position == 4)
            fragment = new WorkExperienceActivity();
        else if (position == 5)
            fragment = new ProjectsActivity();
        else if (position == 6)
            fragment = new SkillsActivity();
        else if (position == 7)
            fragment = new AchievementsActivity();
        else if (position == 8)
            fragment = new InterestsActivity();
        else if (position == 9)
            fragment = new LanguagesActivity();
        else if (position == 10)
            fragment = new DeclarationActivity();

        return fragment;
    }

    @Override
    public int getCount() {
        return 11;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
            title = "Objective";
        else if (position == 1)
            title = "Personal Details";
        else if (position == 2)
            title = "Template";
        else if (position == 3)
            title = "Education";
        else if (position == 4)
            title = "Experience";
        else if (position == 5)
            title = "Projects";
        else if (position == 6)
            title = "Skills";
        else if (position == 7)
            title = "Achievements";
        else if (position == 8)
            title = "Interests";
        else if (position == 9)
            title = "Languages";
        else if (position == 10)
            title = "Declaration";
        return title != null ? title.toLowerCase() : null;
    }
}