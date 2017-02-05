import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.voucher.model.ProductCategoryRestrictionModel
import de.hybris.platform.voucher.model.RestrictionModel
import de.hybris.platform.voucher.model.VoucherModel
import de.hybris.platform.ruleengine.dao.impl.DefaultEngineRuleDao
import de.hybris.platform.ruleengineservices.enums.RuleStatus
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel

import groovy.json.JsonSlurper



def script = new GroovyScriptEngine( '.' ).with {
    loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script


/******************Verifying the voucher******************/

def serialVoucherCode = 'sv1'
def euroISOcode = 'EUR'
def voucherValue = 10
def numOfVouchers = 50
def restrictionTypeClass = ProductCategoryRestrictionModel.class
def restrictionCategoryCode = 'drama'

Collection<VoucherModel> vouchers = voucherService.getAllVouchers()
def serialVoucher = vouchers.find {voucher -> 
	voucher.getCode().equals(serialVoucherCode)}

if (serialVoucher != null){
	addLog("OK: serial voucher with ID ${serialVoucherCode} exists.")
	
	if (!serialVoucher.getAbsolute()){
		addError('NOT OK: the voucher is not absolute!')
	}
	
	if (!serialVoucher.getCurrency().getIsocode().equals(euroISOcode)){
		addError("NOT OK: the voucher's currency is not correct! It should be ${euroISOcode}.")
	}
	
	if (serialVoucher.getValue() != voucherValue){
		addError("NOT OK: the voucher's value is not correct! It should be ${voucherValue}.")
	}
	
	def numOfExistingVouchers = serialVoucher.getCodes().size()
	if (numOfExistingVouchers < 1){
		addError("NOT OK: there are no (serial) voucher codes generated! Make sure that you have generated the serial vouchers manually in the Backoffice!")
	}
	
	Set<RestrictionModel> restrictions = serialVoucher.getRestrictions()
	if (restrictions != null || restrictions.size() == 1){		
		def restriction = restrictions.getAt(0)
		if (restriction.getClass() == restrictionTypeClass){
			Collection<Category> categories = restriction.getCategories()
			if (categories != null && categories.size() == 1){
				def category = categories.getAt(0)
				if (category.getCode().equals(restrictionCategoryCode)){
					addLog('OK: restriction is created with correct values.')
				} else {
					addError("NOT OK: restriction doesn't have the correct category! It should be ${restrictionCategory}!")
				} 
			} else if (categories == null || categories.empty){
				addError('NOT OK: no category is defined for the restriction!')
			} else{
				addError('NOT OK: too many categories in the restriction!')
			}
		} else {
			addError("NOT OK: no restriction of type ${restrictionTypeClass.getSimpleName()} is defined for the voucher!")
		}
		
	} else if (restrictions == null || restrictions.empty){
		addError('NOT OK: no restriction is defined for the voucher!')
	} else {
		addError('NOT OK: too many restrictions are defined for the voucher!')
	}
	
	if (!hasError()){
		addLog('OK: voucher is created with correct values.')
	}
} else{
	addError("NOT OK: no serial voucher with the identifier ${serialVoucherCode}.")
}

/*****************Verifying the Promotion*****************/

def promotionCode = 'freebook01'
def promotionGroupID = 'bookstorePromoGrp'
def orderThreshold = 100
// euroISOcode is defined in the previous block
def orderThresholdCurrency = euroISOcode
def conditionTypeString = 'y_cart_total'
def actionTypeString = 'y_free_gift'
def numberOfFreeGifts = 1


def sourceRule
try{
	 sourceRule = ruleDao.findRuleByCode("freebook01")
} catch (ModelNotFoundException e){
	addError("NOT OK: there's no promotion with the identifier ${promotionCode} in any promotion group! Remember to publish your rule manually in the Backoffice.")
}
if (sourceRule != null && sourceRule.class.simpleName == "PromotionSourceRuleModel"){
	addLog("OK: the promotion rule is created!")
	
	def promotionSourceRule = (PromotionSourceRuleModel) sourceRule
	
	if (sourceRule.website.identifier == promotionGroupID){
		addLog("OK: the promotion rule is in ${promotionGroupID}.")
	} else{
		addError("NOT OK: the promotion rule is not in ${promotionGroupID}! It's in ${sourceRule.website.identifier} instead!")
	}
	
	if (sourceRule.status == RuleStatus.PUBLISHED){
		addLog("OK: the promotion is successfully published.")
	} else{
		addError("NOT OK: the promotion is not published! It is ${sourceRule.status.code} right now.  Make sure that you have published your promotion rule in the Backoffice!")
	}
	
	
	def slurper = new JsonSlurper() //for parsing conditions and actions of a rule
	
	def conditions = sourceRule.conditions
	if (conditions != null){
		conditionsJson = slurper.parseText( conditions )
				
		if (conditionsJson.size == 0) {
			addError("NOT OK: no condition is defined for the promotion rule!")
		} else if (conditionsJson.size > 1){
			addError("NOT OK: too many conditions are set for the promotion rule! There should be only one condition.")
		}
		
		def conditionJson = conditionsJson.get(0)
		def curConditionTypeString = conditionJson.definitionId.toString()
		
		if (curConditionTypeString == conditionTypeString){
			addLog("OK: The condition set for the rule is 'Cart total'.")
		
			if (conditionJson.parameters.operator.value == 'GREATER_THAN_OR_EQUAL' &&
					conditionJson.parameters.value.value.get(orderThresholdCurrency) == orderThreshold){
				addLog("OK: The condition properties are set correctly.")
			} else{
				addError("NOT OK: The condition doesn't have correct values!")
			}
		} else{
			addError("NOT OK: The condition set for the rule is not 'Cart total' but ${curConditionTypeString}!")
		}
		
	} else {
		addError("NOT OK: no condition is defined for the promotion rule!")
	}
	
	def actions = sourceRule.actions
	if (actions != null){
		actionsJson = slurper.parseText( actions )
				
		if (actionsJson.size == 0) {
			addError("NOT OK: no action is defined for the promotion rule!")
		} else if (actionsJson.size > 1){
			addError("NOT OK: too many actions are set for the promotion rule! There should be only one action.")
		}
		
		def actionJson = actionsJson.get(0)
		def curActionTypeString = actionJson.definitionId.toString()
		
		if (curActionTypeString == actionTypeString){
			addLog("OK: The action set for the rule is 'Free gift'.")
		
			if (actionJson.parameters.product.value != null &&
					actionJson.parameters.quantity.value == numberOfFreeGifts){
				addLog("OK: The action properties are set correctly.")
			} else{
				addError("NOT OK: The action doesn't have correct values! You should set only 1 free book as gift.")
			}
		} else{
			addError("NOT OK: The action set for the rule is not 'Cart total' but ${curActionTypeString}!")
		}
		
	} else {
		addError("NOT OK: no action is defined for the promotion rule!")
	}
} else{
	addError("NOT OK: there's no promotion with the identifier ${promotionCode} in ${promotionGroupID}!")
}

/********************Print the Output*********************/
printOutputLog()