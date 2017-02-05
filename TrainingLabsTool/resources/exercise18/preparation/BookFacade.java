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
package my.bookstore.facades.book;

import java.util.List;

import my.bookstore.facades.product.data.BookData;


public interface BookFacade
{
	List<BookData> getTrendingBooks(int numberOfBooks);
}
