/*
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package my.bookstore.addons.trending.controllers.cms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;

import my.bookstore.facades.product.data.BookData;
import my.bookstore.addons.trending.model.TrendingProductsCarouselComponentModel;
import my.bookstore.addons.trending.controllers.*;
import my.bookstore.facades.book.BookFacade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller("TrendingProductsCarouselComponentController")
@RequestMapping(value = "/view/" + TrendingProductsCarouselComponentModel._TYPECODE + "Controller")
public class TrendingProductsCarouselComponentController extends
		AbstractCMSAddOnComponentController<TrendingProductsCarouselComponentModel>
{
	@Autowired
	private BookFacade bookFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final TrendingProductsCarouselComponentModel component)
	{
		final List<BookData> books = new ArrayList<>();
		books.addAll(bookFacade.getTrendingBooks(TrendingaddonControllerConstants.TRENDING_BOOK_NUM));

		model.addAttribute("title", component.getTitle());
		model.addAttribute("bookData", books);
	}
}
