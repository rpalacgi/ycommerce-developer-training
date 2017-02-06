import javax.xml.xpath.*
import javax.xml.parsers.DocumentBuilderFactory
import java.lang.reflect.Field;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.product.ProductModel;
import my.bookstore.core.setup.CoreSystemSetup;
import de.hybris.platform.core.model.media.MediaModel;


def script = new GroovyScriptEngine( '.' ).with { loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script

def checkModel(String className, Map<String, Class>classFields){
	try{
		addLog('Checking class:'+className)
		Class c=Class.forName(className);
		Field [] fields=c.getDeclaredFields()
		Map<String, Class> fieldsMap=new HashMap<>();

		for(def field:fields){
			fieldsMap.put(field.getName(),field.getType())
		}

		for(def field: classFields.keySet()){

			if(fieldsMap.containsKey(field)){
				if(fieldsMap.get(field).getClass().equals(classFields.get(field).getClass())){
					addLog('Attribute:'+field+'...........OK')
				}else{
					addError('Attribute:' + field + ' is wrong type. It should be: ' + classFields.get(field) + ' instead of ' + fieldsMap.get(field))
				}
			}else {
				addError('Missing attribute:'+field+' for class:'+className)
			}


		}

	} catch(ClassNotFoundException ex){
		addError('Class not found:'+className)
	}
}

def checkLocalProperties(valuesMap){
	Properties properties = new Properties()
	InputStream inputStream = CoreSystemSetup.getClassLoader().getResourceAsStream("localization/bookstorecore-locales_en.properties")
	if(inputStream==null){
		addError('cannot find bookstorecore-locales_en.properties file')
	}
	else{
		properties.load(inputStream)

		for(entry in valuesMap){
			if(properties."$entry" !=null && properties."$entry" !=""){
				addLog("Checking property $entry ....OK")
			}else{
				addError("Please set property $entry")
			}
		}
	}
}

addLog('\n* 1. Checking Models *\n')

Map<String, Class> rentalModelMap=new HashMap<>();
rentalModelMap.put("RENTALID",Integer.class);
rentalModelMap.put("STARTDATE",Date.class);
rentalModelMap.put("ENDDATE",Date.class);
rentalModelMap.put("CUSTOMER",CustomerModel.class);
rentalModelMap.put("PRODUCT",ProductModel.class);
checkModel("my.bookstore.core.model.RentalModel",rentalModelMap)

Map<String, Class> productModelMap=new HashMap<>();
productModelMap.put("LANGUAGE",String.class);
productModelMap.put("ISBN10",String.class);
productModelMap.put("ISBN13",String.class);
productModelMap.put("PUBLISHER",String.class);
productModelMap.put("PUBLISHEDDATE",Date.class);
productModelMap.put("RENTABLE",Boolean.class);
productModelMap.put("REWARDPOINTS",Integer.class);
checkModel("de.hybris.platform.core.model.product.ProductModel",productModelMap)

Map<String, Class> bookModelMap=new HashMap<>();
bookModelMap.put("EDITION",String.class);
bookModelMap.put("PUBLICATION",Integer.class);
bookModelMap.put("AUTHORS",Collection.class);
checkModel("my.bookstore.core.model.BookModel", bookModelMap)

Map<String, Class> rewardStatusLevelConfigurationModelMap=new HashMap<>();
rewardStatusLevelConfigurationModelMap.put("THRESHOLD",Integer.class);
rewardStatusLevelConfigurationModelMap.put("IMAGE",MediaModel.class);
rewardStatusLevelConfigurationModelMap.put("REWARDSTATUSLEVEL",my.bookstore.core.enums.RewardStatusLevel);
checkModel("my.bookstore.core.model.RewardStatusLevelConfigurationModel",rewardStatusLevelConfigurationModelMap)

Map<String, Class> customerModelMap=new HashMap<>();
customerModelMap.put("POINTS",Integer.class);
customerModelMap.put("REWARDLEVELSTARTDATE",Date.class);
customerModelMap.put("EXPIREDATE",Date.class);
customerModelMap.put("POINTSTONEXTLEVEL",Integer.class);
customerModelMap.put("REWARDSTATUSLEVEL",my.bookstore.core.enums.RewardStatusLevel);
customerModelMap.put("RENTALS",Collection.class);
checkModel("de.hybris.platform.core.model.user.CustomerModel",customerModelMap)

Map<String, Class> userModelMap=new HashMap<>();
userModelMap.put("BOOKS",Collection.class);
checkModel("de.hybris.platform.core.model.user.UserModel", userModelMap)




def propertiesMap=[
	'type.Product.name',
	'type.Product.description',
	'type.Product.language.name',
	'type.Product.language.description',
	'type.Product.ISBN10.name',
	'type.Product.ISBN10.description',
	'type.Product.ISBN13.name',
	'type.Product.ISBN13.description',
	'type.Product.publisher.name',
	'type.Product.publisher.description',
	'type.Product.publishedDate.name',
	'type.Product.publishedDate.description',
	'type.Product.rentable.name',
	'type.Product.rentable.description',
	'type.Product.rewardPoints.name',
	'type.Product.rewardPoints.description',
	'type.Rental.customer.name',
	'type.Rental.customer.description',
	'type.Book.authors.name',
	'type.Book.authors.description',
	'type.User.books.name',
	'type.User.books.description',
	'type.Book.name',
	'type.Book.description',
	'type.Book.edition.name',
	'type.Book.edition.description',
	'type.Book.publication.name',
	'type.Book.publication.description',
	'type.Book.authors.name',
	'type.Book.authors.description',
	'type.Rental.name',
	'type.Rental.description',
	'type.Rental.rentalId.name',
	'type.Rental.rentalId.description',
	'type.Rental.startDate.name',
	'type.Rental.startDate.description',
	'type.Rental.product.name',
	'type.Rental.product.description',
	'type.Customer.name',
	'type.Customer.description',
	'type.Customer.points.name',
	'type.Customer.points.description',
	'type.Customer.rewardLevelStartDate.name',
	'type.Customer.rewardLevelStartDate.description',
	'type.Customer.expireDate.name',
	'type.Customer.expireDate.description',
	'type.Customer.pointsToNextLevel.name',
	'type.Customer.pointsToNextLevel.description',
	'type.Customer.rewardStatusLevel.name',
	'type.Customer.rewardStatusLevel.description',
	'type.RewardStatusLevelConfiguration.name',
	'type.RewardStatusLevelConfiguration.description',
	'type.RewardStatusLevelConfiguration.threshold.name',
	'type.RewardStatusLevelConfiguration.threshold.description',
	'type.RewardStatusLevelConfiguration.image.name',
	'type.RewardStatusLevelConfiguration.image.description',
	'type.RewardStatusLevelConfiguration.rewardStatusLevel.name',
	'type.RewardStatusLevelConfiguration.rewardStatusLevel.description'
]

addLog('\n* 2. Checking localization *\n')
checkLocalProperties(propertiesMap)

printOutputLog()
