package com.momstouch.momstouchbe.domain.shop.application;

import com.momstouch.momstouchbe.domain.discountpolicy.model.repository.DiscountPolicyRepository;
import com.momstouch.momstouchbe.domain.shop.dto.MenuRequest;
import com.momstouch.momstouchbe.domain.shop.dto.MenuResponse;
import com.momstouch.momstouchbe.domain.shop.model.Menu;
import com.momstouch.momstouchbe.domain.shop.model.Shop;
import com.momstouch.momstouchbe.domain.shop.model.repository.ShopRepository;
import com.momstouch.momstouchbe.domain.shop.model.repository.ShopSearchableRepository;
import com.momstouch.momstouchbe.global.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.momstouch.momstouchbe.domain.shop.dto.MenuRequest.*;
import static com.momstouch.momstouchbe.domain.shop.dto.MenuResponse.*;
import static com.momstouch.momstouchbe.domain.shop.dto.ShopResponse.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {

    private final ShopSearchableRepository shopSearchableRepository;
    private final ShopRepository shopRepository;
    private final DiscountPolicyRepository discountPolicyRepository;
    private final FileUploadUtil fileUploadUtil;

    public ShopMenuListResponse searchAllMenuInShop(Long shopId) {
        return ShopMenuListResponse.of(shopSearchableRepository.findWithMenuListByShopId(shopId));
    }

    @Transactional
    public Shop addMenu(Long shopId, MultipartFile image, MenuCreateRequest menuRequest) {
        Shop shop = null;
        try {
            shop = shopSearchableRepository.findWithMenuListByShopId(shopId);
            shop.getDiscountPolicyList();
            String imageURL = fileUploadUtil.uploadMenuImage(image);
            shop.addMenu(
                    menuRequest.toEntity(
                            imageURL,
                            discountPolicyRepository.getReferenceById(menuRequest.getDiscountPolicyId())
                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shop;
    }

    public MenuDetailSearchResponse searchMenuDetail(Long shopId, Long menuId) {

        Menu menu = shopSearchableRepository.findMenuByMenuId(menuId);
        return MenuDetailSearchResponse.of(menu);
    }

    @Transactional
    public void updateMenuDetail(Long shopId, Long menuId, MultipartFile image, MenuUpdateRequest menuUpdateRequest) {
        try {
            String imageURL = null;
            if(image != null) {
                imageURL = fileUploadUtil.uploadMenuImage(image);
            }

            Menu updatedMenu = menuUpdateRequest.toEntity(
                    imageURL,
                    discountPolicyRepository.getReferenceById(menuUpdateRequest.getDiscountPolicyId())
            );

            Menu originMenu = shopSearchableRepository.findMenuWithOptionGroupByMenuId(menuId);
            System.out.println(originMenu.getOptionGroupList().get(0).getOptionList().get(0).getName());

            originMenu.update(updatedMenu);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
