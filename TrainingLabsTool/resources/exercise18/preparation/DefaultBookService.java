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
package my.bookstore.core.services.impl;

import java.util.List;

import my.bookstore.core.daos.RentalDao;
import my.bookstore.core.model.BookModel;
import my.bookstore.core.services.BookService;



public class DefaultBookService implements BookService
{


	private RentalDao rentalDao;

	/**
	 * @return the rentalDao
	 */
	public RentalDao getRentalDao()
	{
		return rentalDao;
	}

	/**
	 * @param rentalDao
	 *           the rentalDao to set
	 */
	public void setRentalDao(final RentalDao rentalDao)
	{
		this.rentalDao = rentalDao;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see my.bookstore.core.services.BookService#getTrendingBooks(int)
	 */
	@Override
	public List<BookModel> getTrendingBooks(final int numberOfBooks)
	{
		return rentalDao.getMostRentedBooks(numberOfBooks);
	}



}
