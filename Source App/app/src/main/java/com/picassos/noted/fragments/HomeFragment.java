package com.picassos.noted.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.picassos.noted.R;
import com.picassos.noted.activities.MainActivity;
import com.picassos.noted.fragments.home.NotesFragment;
import com.picassos.noted.fragments.home.TodosFragment;

public class HomeFragment extends Fragment {

    // View view
    View view;

    // fragments
    final Fragment notes_fragment = new NotesFragment();
    final Fragment todos_fragment = new TodosFragment();

    // active fragment
    Fragment active = notes_fragment;

    // bottom navigation
    BottomNavigationView bottom_navigation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bottom navigation
        bottom_navigation = view.findViewById(R.id.bottom_navigation);

        FragmentManager fragment_manager = getChildFragmentManager();

        // fragment manager
        fragment_manager.beginTransaction().add(R.id.fragment_container, todos_fragment, "2").hide(todos_fragment).commit();
        fragment_manager.beginTransaction().add(R.id.fragment_container, notes_fragment, "1").commit();

        // bottom navigation on item selected listener
        bottom_navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.notes:
                    fragment_manager.beginTransaction().hide(active).show(notes_fragment).commit();
                    active = notes_fragment;
                    ((MainActivity) requireActivity()).moreOptions.setVisibility(View.VISIBLE);
                    ((MainActivity) requireActivity()).toolbarTitle.setText(getString(R.string.app_name));
                    break;
                case R.id.todos:
                    fragment_manager.beginTransaction().hide(active).show(todos_fragment).commit();
                    active = todos_fragment;
                    ((MainActivity) requireActivity()).moreOptions.setVisibility(View.GONE);
                    ((MainActivity) requireActivity()).toolbarTitle.setText(getString(R.string.all_todos));
                    break;
            }
            return true;
        });

        // bottom navigation on item reselected
        bottom_navigation.setOnNavigationItemReselectedListener(item -> {
            switch (item.getItemId()) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


}