/*
 * Copyright 2018 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.diva.tocorastorage;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.diva.tocorastorage.DivaToCoraConverter;
import se.uu.ub.cora.diva.tocorastorage.DivaToCoraConverterFactory;
import se.uu.ub.cora.diva.tocorastorage.DivaToCoraConverterFactoryImp;
import se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverter;
import se.uu.ub.cora.diva.tocorastorage.NotImplementedException;

public class DivaToCoraConverterFactoryTest {
	private DivaToCoraConverterFactory alvinToCoraConverterFactoryImp;

	@BeforeMethod
	public void beforeMethod() {
		alvinToCoraConverterFactoryImp = new DivaToCoraConverterFactoryImp();
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "No converter implemented for: someType")
	public void factorUnknownTypeThrowsException() throws Exception {
		alvinToCoraConverterFactoryImp.factor("someType");
	}

	@Test
	public void testFactoryPlace() throws Exception {
		DivaToCoraConverter converter = alvinToCoraConverterFactoryImp.factor("place");
		assertTrue(converter instanceof DivaToCoraPersonConverter);
	}
}
