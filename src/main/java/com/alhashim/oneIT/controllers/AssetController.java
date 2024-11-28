package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.ImportAssetDto;
import com.alhashim.oneIT.models.Asset;

import com.alhashim.oneIT.repositories.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/asset")
public class AssetController {

    @Autowired
    AssetRepository assetRepository;

    @GetMapping("/list")
    public String assetList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<Asset> assetPage;

        ImportAssetDto importAssetDto = new ImportAssetDto();

        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            assetPage = assetRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all employees with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            assetPage = assetRepository.findAll(pageable);
        }
        model.addAttribute("assets", assetPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", assetPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", assetPage.getTotalElements());
        model.addAttribute("pageTitle","Assets List");
        model.addAttribute("importAssetDto", importAssetDto);
        return "asset/list";
    }
}
