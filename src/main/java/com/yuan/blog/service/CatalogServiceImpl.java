package com.yuan.blog.service;

import com.yuan.blog.domain.Catalog;
import com.yuan.blog.domain.User;
import com.yuan.blog.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Catalog 服务接口实现.
 */
@Service
public class CatalogServiceImpl implements CatalogService {
	@Autowired
	private CatalogRepository catalogRepository;

	@Override
	public Catalog saveCatalog(Catalog catalog) {
		// 用mybatis
//		Blog blog = blogDao.selectByPrimaryKey(1);

		// 判断重复
        List<Catalog> list = catalogRepository.findByUserAndName(catalog.getUser(),
        		catalog.getName());
        if(list !=null && list.size() > 0) {
            throw new IllegalArgumentException("该分类已经存在了");
        }
        return catalogRepository.save(catalog);
	}
	
	@Override
	public void removeCatalog(Long id) {
		catalogRepository.deleteById(id);
	}

	@Override
	public Optional<Catalog> getCatalogById(Long id) {
		return catalogRepository.findById(id);
	}

	@Override
	public List<Catalog> listCatalogs(User user) {
		return catalogRepository.findByUser(user);
	}

}
