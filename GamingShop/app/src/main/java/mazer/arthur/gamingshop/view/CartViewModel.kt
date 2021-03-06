package mazer.arthur.gamingshop.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mazer.arthur.gamingshop.data.GamesRepository
import mazer.arthur.gamingshop.data.remote.Response
import mazer.arthur.gamingshop.domain.usecases.*
import mazer.arthur.gamingshop.utils.listeners.CheckoutListener
import mazer.arthur.gamingshop.utils.listeners.ShippingChangedListener

class CartViewModel(private val gamesRepository: GamesRepository): ViewModel() , ShippingChangedListener,
CheckoutListener{

    sealed class ViewEvent {
        class ShippingValueChanged(val quant: Int): ViewEvent()
        object ShippingCartEmpty: ViewEvent()
        object CheckoutSuccessful: ViewEvent()
        object CheckoutFailure: ViewEvent()
        object CheckouCartEmpty: ViewEvent()
    }

    var eventLiveData = MutableLiveData<ViewEvent>()

    var getOriginalValueList = gamesRepository.getTotalOriginalPriceSumCartLiveData()
    var getDiscountValueList = gamesRepository.getTotalDiscountSumCartLiveData()
    var getTotalItemsCart = gamesRepository.getTotalItemsCartLiveData()

    fun getCartList() = liveData(Dispatchers.IO){
        emit(Response.loading())
        try{
            emit(Response.success(gamesRepository.getCartList()))
        } catch (ex: Exception){
            emit(Response.error(null, "Error fetching cart list"))
        }
    }

    fun removeItemCart(id: Int){
        //remove item do carrinho
        val removeItemCartUseCase = RemoveFromCartUseCase(gamesRepository)
        GlobalScope.launch {
            removeItemCartUseCase.removeItem(id)
        }
        //calcula o valor do frete novamente
        calculateShippingValue()
    }

    fun updateGameQuanity(id: Int, value: Int){
        //atualizar a quantidade de itens no carrinho
        val updateGameQuantityUseCase = UpdateGameQuantityUseCase(gamesRepository)
        GlobalScope.launch {
            updateGameQuantityUseCase.updateQuantity(id, value)
        }
        //calcula o valor do frete novamente
        calculateShippingValue()
    }

    fun checkout(){
        val checkoutUseCase = CheckoutUseCase(gamesRepository, this)
        GlobalScope.launch {
            checkoutUseCase.checkout()
        }
    }

    fun calculateShippingValue(){
        val shippingUseCase = ShippingCalculatorUseCase(gamesRepository, this)
        GlobalScope.launch {
            shippingUseCase.getShippingValue()
        }
    }

    override fun onShippingValueChanged(value: Int) {
        eventLiveData.postValue(ViewEvent.ShippingValueChanged(value))
    }

    override fun emptyCart() {
        eventLiveData.postValue(ViewEvent.ShippingCartEmpty)
    }

    override fun checkoutSuccessful() {
        eventLiveData.postValue(ViewEvent.CheckoutSuccessful)
    }

    override fun checkoutFailure() {
        eventLiveData.postValue(ViewEvent.CheckoutFailure)
    }

    override fun cartIsEmpty() {
        eventLiveData.postValue(ViewEvent.CheckouCartEmpty)
    }


}