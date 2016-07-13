import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vitaliy Lutchenko on 01.12.2015.
 */
public class FragmentManualSearch extends FragmentDialogs implements View.OnClickListener,
        FragmentDialogList.OnDialogItemSelectListener,
        LoaderManager.LoaderCallbacks<Cursor> {


    public static final String DIVISION = "fragmentManualSearchDivision";
    public static final String PRODUCTS = "fragmentManualSearchProducts";
    private static final String FR_PARAMS = "fragmentManualSearchParams";
    private ViewHolder mHolder;
    private Divisions divisions;
    private JProducts products;
    private FragmentManualSearchParameters params;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_search, container, false);
        LogOut.initLogout(getActivity(), view);
        PreferenceHelper.initBackBTN(getActivity(), view);
        mHolder = new ViewHolder(view);
        initToggleButton();
        initDateModule();
        mHolder.getStoreName().setText(PreferenceHelper.getStoreName(getContext()));
        mHolder.getDivision().setOnClickListener(this);
        mHolder.getDepartment().setOnClickListener(this);
        mHolder.getCategory().setOnClickListener(this);
        mHolder.getSearch().setOnClickListener(this);
        mHolder.getClearDivision().setOnClickListener(this);
        mHolder.getClearDepartment().setOnClickListener(this);
        mHolder.getClearCategory().setOnClickListener(this);
        return view;
    }

    private void initDateModule() {
        mHolder.getDateFilter().attachActivity((AppCompatActivity) getActivity());
    }

    private void initToggleButton() {
        mHolder.mSearchToggle.setOnClickListener(this);
    }

    private void setToggleButtonText(boolean isAdvancedMode) {
        SpannableString text = new SpannableString(getString(isAdvancedMode ? R.string.toggle_simple_search : R.string.toggle_advanced_search));
        text.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mHolder.mSearchToggle.setText(text);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            divisions = Divisions.getDivisionsFromArguments(getArguments(), DIVISION);
            products = JProducts.getProductsFromArguments(getArguments(), PRODUCTS);
            params = new FragmentManualSearchParameters();

        } else {
            divisions = Divisions.getDivisionsFromArguments(savedInstanceState, DIVISION);
            products = JProducts.getProductsFromArguments(savedInstanceState, PRODUCTS);
            params = savedInstanceState.getParcelable(FR_PARAMS);
        }

        setSearchMode(params.isAdvancedMode());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DIVISION, divisions);
        outState.putParcelable(PRODUCTS, products);
        outState.putParcelable(FR_PARAMS, params);
    }

    @Override
    public void onClick(View v) {
        ArrayList<CodeDescriptionPair> data;
        int division;
        int department;
        switch (v.getId()) {
            case R.id.division:
                FragmentDialogList.registerOnDialogItemSelectListener(this);
                data = divisions.getDivisionsMap();
                FragmentDialogList
                        .newInstance(LIST_TYPE.DIVISION, data)
                        .show(getActivity().getSupportFragmentManager(), "");
                break;
            case R.id.department:
                if (params.getDivision() != null) {
                    FragmentDialogList.registerOnDialogItemSelectListener(this);
                    division = params.getDivision().getCode();
                    data = divisions.getDepartmentsMapByDivisionCode(division);
                    FragmentDialogList
                            .newInstance(LIST_TYPE.DEPARTMENT, data)
                            .show(getActivity().getSupportFragmentManager(), "");
                }
                break;
            case R.id.category:
                if (params.getDivision() != null && params.getDepartment() != null) {
                    FragmentDialogList.registerOnDialogItemSelectListener(this);
                    division = params.getDivision().getCode();
                    department = params.getDepartment().getCode();
                    data = divisions.getCategoriesMapByDepartmentCode(division, department);
                    FragmentDialogList
                            .newInstance(LIST_TYPE.CATEGORIES, data)
                            .show(getActivity().getSupportFragmentManager(), "");
                }
                break;
            case R.id.search:
                if (mHolder.getDateFilter().isShowAll()) {
                    showResults(-1, -1);
                } else {
                    if (mHolder.getDateFilter().isDatesValid()) {
                        long from = mHolder.getDateFilter().getDateFrom();
                        long to = mHolder.getDateFilter().getDateTo();
                        showResults(from, to);
                    }
                }
                break;
            case R.id.clear_division:
                clearDivision();
                clearDepartment();
                clearCategory();
                break;
            case R.id.clear_department:
                clearDepartment();
                clearCategory();
                break;
            case R.id.clear_category:
                clearCategory();
                break;
            case R.id.searchToggle:
                params.setIsAdvancedMode(!params.isAdvancedMode());
                setSearchMode(params.isAdvancedMode());

        }
    }

    private void clearDivision() {
        params.setDivision(null);
        setTextStyle(mHolder.getDivision(), LIST_TYPE.DIVISION, null);
        mHolder.getClearDivision().setVisibility(View.INVISIBLE);
    }

    private void clearDepartment() {
        params.setDepartment(null);
        setTextStyle(mHolder.getDepartment(), LIST_TYPE.DEPARTMENT, null);
        mHolder.getClearDepartment().setVisibility(View.INVISIBLE);
    }

    private void clearCategory() {
        params.setCategory(null);
        setTextStyle(mHolder.getCategory(), LIST_TYPE.CATEGORIES, null);
        mHolder.getClearCategory().setVisibility(View.INVISIBLE);
    }

    private void showResults(long from, long to) {
        String keyword = mHolder.getKeyword().getText().toString();
        Bundle args = new Bundle();
        SelectionBuilder builder = SelectionBuilder.fromParams(params.isAdvancedMode(), keyword, params, from, to);
        args.putParcelable(SelectionBuilder.BUNDLE, builder);
        if (getLoaderManager().getLoader(LOAD_MANUAL) == null) {
            getLoaderManager().initLoader(LOAD_MANUAL, args, this);
        } else {
            getLoaderManager().restartLoader(LOAD_MANUAL, args, this);
        }
    }

    private void startActivityProducts(SelectionBuilder queryBuilder) {
        Intent intent = new Intent(getActivity(), ActivitySearchResult.class)
                .putExtra(FragmentSearchResult.SEARCH_QUERY, queryBuilder);
        startActivity(intent);
    }

    private void setSearchMode(boolean isAdvancedMode) {
        setToggleButtonText(isAdvancedMode);
        mHolder.mModeTitle.setText(getString(isAdvancedMode ? R.string.search_advanced : R.string.search_manual));
        mHolder.mAdvancedSearch.setVisibility(isAdvancedMode ? View.VISIBLE : View.GONE);
        mHolder.mKeywordSearch.setVisibility(isAdvancedMode ? View.GONE : View.VISIBLE);
    }


    @Override
    public void dialogItemSelectResult(LIST_TYPE type, CodeDescriptionPair selected) {
        FragmentDialogList.unregisterOnDialogItemSelectListener();
        switch (type) {
            case DIVISION:
                params.setDivision(selected);
                params.setDepartment(null);
                params.setCategory(null);
                mHolder.getClearDivision().setVisibility(View.VISIBLE);
                mHolder.getClearDepartment().setVisibility(View.INVISIBLE);
                mHolder.getClearCategory().setVisibility(View.INVISIBLE);
                setTextStyle(mHolder.getDivision(), type, selected.getDescription());
                setTextStyle(mHolder.getDepartment(), LIST_TYPE.DEPARTMENT, null);
                setTextStyle(mHolder.getCategory(), LIST_TYPE.CATEGORIES, null);
                break;
            case DEPARTMENT:
                params.setDepartment(selected);
                params.setCategory(null);
                mHolder.getClearDepartment().setVisibility(View.VISIBLE);
                mHolder.getClearCategory().setVisibility(View.INVISIBLE);
                setTextStyle(mHolder.getDepartment(), type, selected.getDescription());
                setTextStyle(mHolder.getCategory(), LIST_TYPE.CATEGORIES, null);
                break;
            case CATEGORIES:
                params.setCategory(selected);
                mHolder.getClearCategory().setVisibility(View.VISIBLE);
                setTextStyle(mHolder.getCategory(), type, selected.getDescription());
                break;
        }
    }

    private void setTextStyle(TextView view, LIST_TYPE type, String text) {
        int textColor = getResources().getColor(text == null ? R.color.back_text : R.color.black);
        Drawable icon = text == null ? getResources().getDrawable(type.draw) : null;
        String txt = text == null ? getString(type.txt) : text;
        view.setTextColor(textColor);
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        view.setText(txt);
    }

    private static final int LOAD_MANUAL = 2;
    private SelectionBuilder sb;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOAD_MANUAL) {
            sb = args.getParcelable(SelectionBuilder.BUNDLE);
            if (sb != null) {
                showDialogSearching();
                return new CursorLoader(getActivity(), Products.CONTENT_URI_SEARCH, Products.projection_search_with_url, sb.buildSelection(), sb.buildSelectionArgs(), null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOAD_MANUAL:
                hideDialog();
                if (data.getCount() == 0) {
                    AlertDialogs.showSearchErrorDialog(getActivity());
                } else {
//                    clearParams();
                    startActivityProducts(sb);
                }
                break;
        }
    }

    private void clearParams() {
        clearDivision();
        clearDepartment();
        clearCategory();
        params.setKeyword("");
        mHolder.getKeyword().setText("");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        hideDialog();
    }

    private class ViewHolder {
        private LinearLayout mAdvancedSearch;
        private LinearLayout mKeywordSearch;
        private DateFilterView mDateFilter;
        private TextView mModeTitle;
        private TextView mSearchToggle;
        private ImageView mSearch;
        private EditText mKeyword;
        private TextView mDivision;
        private ImageView mClearDivision;
        private TextView mDepartment;
        private ImageView mClearDepartment;
        private TextView mCategory;
        private ImageView mClearCategory;
        private TextView mStoreName;

        public ViewHolder(View view) {
            mModeTitle = (TextView) view.findViewById(R.id.modeTitle);
            mStoreName = (TextView) view.findViewById(R.id.storeName);
            mAdvancedSearch = (LinearLayout) view.findViewById(R.id.advanced_search);
            mKeywordSearch = (LinearLayout) view.findViewById(R.id.keyword_search);
            mDateFilter = (DateFilterView) view.findViewById(R.id.dateFilter);
            mSearchToggle = (TextView) view.findViewById(R.id.searchToggle);
            mSearch = (ImageView) view.findViewById(R.id.search);
            mKeyword = (EditText) view.findViewById(R.id.keyword);
            mDivision = (TextView) view.findViewById(R.id.division);
            mClearDivision = (ImageView) view.findViewById(R.id.clear_division);
            mDepartment = (TextView) view.findViewById(R.id.department);
            mClearDepartment = (ImageView) view.findViewById(R.id.clear_department);
            mCategory = (TextView) view.findViewById(R.id.category);
            mClearCategory = (ImageView) view.findViewById(R.id.clear_category);
        }

        public LinearLayout getAdvancedSearch() {
            return mAdvancedSearch;
        }

        public LinearLayout getKeywordSearch() {
            return mKeywordSearch;
        }

        public DateFilterView getDateFilter() {
            return mDateFilter;
        }

        public TextView getSearchToggle() {
            return mSearchToggle;
        }

        public ImageView getSearch() {
            return mSearch;
        }

        public TextView getDivision() {
            return mDivision;
        }

        public TextView getDepartment() {
            return mDepartment;
        }

        public TextView getCategory() {
            return mCategory;
        }

        public EditText getKeyword() {
            return mKeyword;
        }

        public ImageView getClearDivision() {
            return mClearDivision;
        }

        public ImageView getClearDepartment() {
            return mClearDepartment;
        }

        public ImageView getClearCategory() {
            return mClearCategory;
        }

        public TextView getStoreName() {
            return mStoreName;
        }
    }
}
