package kashyap.anurag.frenzystore.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import kashyap.anurag.frenzystore.Fragments.ProductDescriptionFragment;
import kashyap.anurag.frenzystore.Fragments.ProductSpecificationFragment;

public class ProductDescriptionAdapter extends FragmentPagerAdapter {

    private int totalTabs;

    public ProductDescriptionAdapter(@NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ProductDescriptionFragment productDescriptionFragment1 = new ProductDescriptionFragment();
                return productDescriptionFragment1;
            case 1:
                ProductSpecificationFragment productSpecificationFragment = new ProductSpecificationFragment();
                return productSpecificationFragment;
            case 2:
                ProductDescriptionFragment productDescriptionFragment2 = new ProductDescriptionFragment();
                return productDescriptionFragment2;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
