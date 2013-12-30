$(document).ready(function ()
{
    var orderTotal = 0.0;

    $('#hotdog').popover();
    $('#chipotle').popover();
    $('#chipotle1').popover();
    $('#chickfila').popover();
    $('#chickfila1').popover();
    $('#port-1').popover();
    $('#port-1-1').popover();
    $('#pizza-1').popover();
    $('#subway').popover();
    $('#subway1').popover();
    $('#kfc').popover();
    $('#kfc1').popover();
    $('#flame').popover();
    $('#flame1').popover();
    $('#port-2').popover();
    $('#pizza-2').popover();
    $('#taq').popover();
    $('#smoothie').popover();
    $('#pickup').popover();
    $('#carls').popover();

    $("#submit").click(function ()
    {
        submitOrder();
    });

    // hide the lunch form and submit button on load
    $('#lunch-form').hide();
    $('#submit').hide();

    // set the typeahead name field
    setUpTypeAhead();


    $('#grade').change(function ()
    {
        //lookupNames();
        checkForFieldTrip();
    });

    $('#enter').click(function ()
    {
        // show the form and submit button
        submitNameAndGrade();
        $('#lunch-form').show();
        $('#submit').show();
    });

    // total the order cost while adding items
    $('.select-yes-no').change(function ()
    {
        var name = this.name;
        var value = this.value;
        //console.log("--==-select change name/value: " + name + "/" + value);
        if (name == "chickfila-order")
        {
            //console.log("name = chickfila");
            if (value == true)
            {
                orderTotal = orderTotal + 4.50;
            }
            else if (value == false)
            {
                orderTotal = orderTotal - 4.50;
            }
        }
        //console.log("orderTotal: " + orderTotal);
        switch (name)
        {
            case "chickfila-order":
            case "chickfila1-order":
            case "flame-order":
            case "flame1-order":
            case "kfc-order":
            case "kfc1-order":
            case "port-1-order":
            case "port-1-1-order":
            case "port-2-order":
            case "port-2-2-order":
            case "pickup-order":
            case "smoothie-order":
            case "subway-order":
            case "subway1-order":
                if (value == true)
                {
                    orderTotal += 4.50;
                }
                else if (value == false)
                {
                    orderTotal -= 4.50;
                }
                break;
            case "carls":
            case "hotdog":
            case "taq":
            case "pizza-1":
            case "pizza-2":
                if (value == true)
                {
                    orderTotal += 3.75;
                }
                else if (value == false)
                {
                    orderTotal -= 1.75;
                }
                break;
            case "chipotle":
            case "chipotle1":
                // get the choice1 value
                var name = "#chipotle;"
                var choice = $(name + '-choice-1').val();
                //var choice = $('#chipotle-choice-1').val();
                if (choice == "one" && value == "true")
                {
                    orderTotal += 3.75;
                }
                else if (choice == "one" && value == "false")
                {
                    orderTotal -= 3.75;
                }
                else if (choice == "two" && value == "true")
                {
                    orderTotal += 4.50;
                }
                else if (choice == "two" && value == "false")
                {
                    orderTotal -= 4.50;
                }
                break;

            console.log("orderTotal: " + orderTotal);
        }
    });

    /**
     * hide the "Add another" button for now....
     */
    $('#another').hide();
});

function submitOrder()
{
    var name = $('#name').val();
    //console.log("name: " + name);
    if (name == "")
    {
        alert("Please Enter a Name");
        $('#name').focus();
        return false;
    }

    var grade = $('#grade').val();
    //console.log("grade: " + grade);
    if (grade == "Select Grade")
    {
        alert("Please Select a Grade");
        $('#grade').focus();
        return false;
    }

    if ($('#agreement').is(":checked"))
    {
        //console.log("it's checked");
    }
    else
    {
        //console.log("it's NOT checked");
        alert("Please accept the Agreement before submitting your Hot Lunch Order");
        $('#agreement').focus();
        return false;
    }

    //var str = $('#lunch-form').serialize();
    //console.log("str: " + str);

    $.ajax(
            {
                type: 'POST',
                url: '/dispatcher/addLunchOrder',
                cache: false,
                async: false,
                data: $('#lunch-form').serialize(),
                dataType: "json",
                success: function (result)
                {
                    if (result != null)
                    {
                        window.location.assign('lunch-confirm.html');
                    }
                    else
                    {
                        alert("We were unable to save your Hot Lunch Order, please try again");
                    }
                }
            });
}

function checkForFieldTrip()
{
    var grade = $('#grade').val();
    if (grade == "4")
    {
        ////console.log("grade 6 selected...");
        //$('.field-trip').css('display', 'none');
        $('.field-trip').hide();
    }
    else
    {
        //console.log("grade 6 NOT selected...");
        $('.field-trip').show();
    }
}

function submitNameAndGrade()
{
    var name = $('#name').val();
    var grade = $('#grade').val();

    $.ajax({
        type: 'POST',
        url: '/dispatcher/saveNameAndGrade',
        data: {name: name, grade: grade},
        cache: false,
        async: false,
        dataType: "json",
        success: function (data)
        {
            if (data != null)
            {
                //console.log("data: " + data);
            }
        }
    });
}

/**
function setUpTypeAhead()
{
    $('#name').typeahead({
        items: 8,
        minLength: 2,
        source: function (query, process)
        {
            var qry = query; //.match(/\w+|"[^"]+"/g).pop();
            if (qry.lastIndexOf(' ') > 0)
            {
                return false;
            }
            else
            {
                var grade = $('#grade').val();
                return $.get('dispatcher/getNames', { grade: grade, q: qry }, function (data)
                {
                    return process(data);
                });
            }
        }
    });
}
 **/

