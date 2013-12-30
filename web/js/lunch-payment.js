$(document).ready(function ()
{
    $('#submit-lookup').click(function ()
    {
        getOrderInfo();
    });

    $('#submit-payment').click(function ()
    {
        submitPaymentInfo();
    });

    // set the typeahead name field
    setUpTypeAhead();

    $('#result').hide();

});

function getOrderInfoNew()
{
    var grade = $('#grade').val();
    //console.log("grade: " + grade);
    if (grade == "Select Grade")
    {
        alert("Please Select a Grade");
        $('#grade').focus();
        return false;
    }

    var month = $('#month').val();
    if (month == "Select Month")
    {
        alert("Please Select a Month");
        $('#month').focus();
        return false;
    }
    $.ajax(
        {
            type: 'POST',
            url: '/dispatcher/getOrdersForGradeAndMonth',
            data: {grade: grade, month: month},
            cache: false,
            async: false,
            dataType: "json",
            success: function (orderItemList)
            {
                if (orderItemList != null)
                {
                    var buffer = [];
                    buffer.push('<table class="table table-bordered table-striped">');
                    buffer.push('<thead> <tr>');
                    buffer.push('<th>Name</th>');
                    buffer.push('<th>Order Total</th>');
                    buffer.push('<th>Order Paid</th>');
                    buffer.push('<th>Amount Paid</th>');
                    buffer.push('<th>Payment Type</th>');
                    buffer.push('<th>Submit</th>');
                    buffer.push('</tr></thead>');

                    buffer.push('<tbody class="body">');

                    $.each(orderItemList, function (index, orderItem)
                    {
                        buffer.push('<tr>')
                        var name = orderItem.studentName;
                        var orderTotal = orderItem.orderTotal.toFixed(2);
                        buffer.push('<td>' + name + '</td>');
                        buffer.push('<td>' + orderTotal + '</td>');

                        var payment = orderItem.paymentInfo;
                        // todo -- make this a drop down
                        if (payment.paid)
                        {
                            buffer.push('<td>true</td>');
                            //$('#paid').val("true");
                        }
                        else
                        {
                            buffer.push('<td>false</td>');
                        }
                        buffer.push('<td>' + orderTotal + '</td>');
                        buffer.push('</tr>')
                    });

                    /**
                     <tr>
                     <td>Caitlyn</td>
                     <td>$28.50</td>
                     <td>Yes/No</td>
                     <td>$28.50</td>
                     <td>Cash/Check</td>
                     <td>
                     <button id="submit-payment-item" class="btn btn-primary">Submit Payment</button>
                     </td>
                     </tr>
                     **/
                }
                else
                {
                    alert("We were unable retrieve the Hot Lunch Orders, please check the Grade and Month and try again");
                }


                if (data != null && data.lunchOrderItemMap != null)
                {
                    var total = data.orderTotal.toFixed(2);
                    var name = data.studentName;
                    var grade = data.studentGrade;

                    /**
                     * check for existing payment information
                     */
                    var payment = data.paymentInfo;
                    if (null != payment)
                    {
                        if (payment.paid)
                        {
                            $('#paid').val("true");
                        }
                        else
                        {
                            $('#paid').val("false");
                        }
                        var amount = '$' + payment.amountPaid.toFixed(2);
                        $('#amount-pay').val(amount);
                        $('#type-pay').val(payment.paymentType);
                    }

                    $('#result').show();
                }
                else
                {
                    alert("We were unable retrieve your Hot Lunch Order, please check the Name and Grade and try again");
                }
            }
        });
}

function getOrderInfo()
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

    var month = $('#month').val();
    if (month == "Select Month")
    {
        alert("Please Select a Month");
        $('#month').focus();
        return false;
    }
    $.ajax(
        {
            type: 'POST',
            url: '/dispatcher/getLunchOrder',
            data: {month: month, name: name, grade: grade},
            cache: false,
            async: false,
            dataType: "json",
            success: function (data)
            {
                if (data != null && data.lunchOrderItemMap != null)
                {
                    var total = data.orderTotal.toFixed(2);
                    var name = data.studentName;
                    var grade = data.studentGrade;

                    $('#name-pay').val(name);
                    if (grade == "K")
                    {
                        $('#grade-pay').val("Kindergarten");
                    }
                    else if (grade == "F")
                    {
                        $('#grade-pay').val("Faculty/Aide");
                    }
                    else
                    {
                        $('#grade-pay').val(grade);
                    }
                    $('#total-pay').val('$' + total);

                    /**
                     * check for existing payment information
                     */
                    var payment = data.paymentInfo;
                    if (null != payment)
                    {
                        if (payment.paid)
                        {
                            $('#paid').val("true");
                        }
                        else
                        {
                            $('#paid').val("false");
                        }
                        var amount = '$' + payment.amountPaid.toFixed(2);
                        $('#amount-pay').val(amount);
                        $('#type-pay').val(payment.paymentType);
                    }

                    $('#result').show();
                }
                else
                {
                    alert("We were unable retrieve your Hot Lunch Order, please check the Name and Grade and try again");
                }
            }
        });
}

function submitPaymentInfo()
{
    var name = $('#name-pay').val();
    var grade = $('#grade-pay').val();
    var month = $('#month').val();
    if (grade == "Kindergarten")
    {
        grade = "K";
    }
    else if (grade == "Faculty/Aide")
    {
        grade = "F";
    }
    var paid = $('#paid').val();
    if (paid == 'no')
    {
        alert("Are you sure you want to submit the order as NOT paid?");
        $('#paid').focus();
        return false;
    }
    var amount = $('#amount-pay').val();
    if (amount.charAt(0) == '$')
    {
        amount = amount.substring(1, amount.length);
    }
    var type = $('#type-pay').val();

    $.ajax(
        {
            type: 'POST',
            url: '/dispatcher/addOrderPayment',
            data: {name: name, grade: grade, month: month, paid: paid, amount: amount, type: type},
            cache: false,
            async: false,
            dataType: "json",
            success: function (data)
            {
                if (data.message == "Success")
                {
                    $('#result').hide();
                    /**
                     * create the bottom section
                     */
                    var bottomBuffer = [];
                    bottomBuffer.push('<p></p><p></p>');
                    bottomBuffer.push('<h4>The payment information was successfully saved</h4>')
                    $("#bottom").html(bottomBuffer);
                }
                else
                {
                    alert("We were unable to save the payment information, please try again");
                }
            }
        });

}

