$(document).ready(function ()
{
    $("#submit").click(function ()
    {
        submitRequest();
    });
});


function submitRequest()
{
    var name = $('#name').val();
    var desc = $('#desc').val();

    var choice = $('#item-choice').val();
    var choice1 = $('#choice-1').val();
    var choice2 = $('#choice-2').val();
    // make sure choice1 and choice2 are populated
    if (choice == "no")
    {
        choice1 = "none";
        choice2 = "none";
    }

    var price = $('#price').val();
    var priceAdd = $('#price-add').val();

    var sides = $('#item-side').val(); // returns an array of selected values
    var condiments = $('#condiments').val(); // returns an array of selected values
    var paperGoods = $('#paper-goods').val(); // returns an array of selected values

    /*
     var company = $('#companySelect').val();
     if (company == "select")
     {
     alert("Please select a company")
     return false;
     }
     */
    console.log("name: " + name);
    console.log("desc: " + desc);
    console.log("choice: " + choice);
    console.log("choice1: " + choice1);
    console.log("choice2: " + choice2);
    console.log("price: " + price);
    console.log("priceAdd: " + priceAdd);
    console.log("sides: " + sides);
    console.log("condiments: " + condiments);
    console.log("paperGoods: " + paperGoods);

    $.ajax(
            {
                type: 'POST',
                url: '/dispatcher/addLunchItem',
                cache: false,
                async: false,
                data: {
                    name: name,
                    desc: desc,
                    choice: choice,
                    choice1: choice1,
                    choice2: choice2,
                    price: price,
                    priceAdd: priceAdd,
                    sides: sides.toString(),
                    condiments: condiments.toString(),
                    paperGoods: paperGoods.toString()
                },
                dataType: "json",
                success: function (result)
                {
                    console.log('result: ' + result);
                    //$("#result").html(feedResult.message);
                }
            });


}
