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
package my.bookstore.core.services;

import java.util.List;

import my.bookstore.core.model.BookModel;


/**
 * Provides services related to a Book
 *
 */
public interface BookService
{

	List<BookModel> getTrendingBooks(final int numberOfBooks);
}
