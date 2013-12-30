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

function getPrettyChoiceName(choice, name)
{
    switch (name)
    {
        case "CARLS":
            if (choice == "cheese")
            {
                return "cheeseburger";
            }
            else if (choice == "regular")
            {
                return "hamburger";
            }
            else if (choice == "chicken")
            {
                return "chicken stars";
            }
            break;
        case "CHICKFILA":
            if (choice == "breaded")
            {
                return "breaded nuggets";
            }
            else if (choice == "grilled")
            {
                return "grilled nuggets";
            }
            break;
        case "CHIPOTLE":
            if (choice == "chicken")
            {
                return "chicken quesadilla";
            }
            else if (choice == "cheese")
            {
                return "cheese quesadilla";
            }
            else if (choice == "one")
            {
                return "one quesadilla";
            }
            else if (choice == "two")
            {
                return "two quesadillas";
            }
            break;
        case "KFC":
            if (choice == "strips")
            {
                return "chicken strips";
            }
            else if (choice == "grilled")
            {
                return "drumstick &amp; leg";
            }
            break;
        case "PICKUPSTIX":
            if (choice == "chicken")
            {
                return "chicken bowl";
            }
            else if (choice == "tofu")
            {
                return "tofu bowl";
            }
            else if (choice == "white")
            {
                return "white rice";
            }
            else if (choice == "brown")
            {
                return "brown rice";
            }
            break;
        case "PIZZA1":
        case "PIZZA2":
            if (choice == "cheese")
            {
                return "cheese pizza";
            }
            else if (choice == "pepp")
            {
                return "pepperoni pizza";
            }
            else if (choice == "salad")
            {
                return "side salad";
            }
            else if (choice == "veggie")
            {
                return "veggie";
            }
            break;
        case "PORTOFINO1":
            if (choice == "cheese")
            {
                return "cheese sauce";
            }
            else if (choice == "meat")
            {
                return "meat sauce";
            }
            break;
        case "PORTOFINO2":
            if (choice == "pasta")
            {
                return "pasta w/ marinara &amp; meatball";
            }
            else if (choice == "lasagna")
            {
                return "lasagna";
            }
            else if (choice == "salad")
            {
                return "salad w/ grilled chicken";
            }
            break;
        case "SMOOTHIE":
            if (choice == "jetty")
            {
                return "strawberry/banana";
            }
            else if (choice == "mango")
            {
                return "mango/pineapple";
            }
            else if (choice == "turkey")
            {
                return "turkey croissant";
            }
            else if (choice == "ham")
            {
                return "ham croissant";
            }
            break;
        case "SUBWAY":
            if (choice == "turkey")
            {
                return "turkey sandwich";
            }
            else if (choice == "ham")
            {
                return "ham sandwich";
            }
            else if (choice == "white")
            {
                return "white roll";
            }
            else if (choice == "wheat")
            {
                return "wheat roll";
            }
            break;
        default:
            return choice;
    }
}

function getPrettyName(name)
{
    if (name == "CARLS")
    {
        return "Carls Jr.";
    }
    else if (name == "CHICKFILA")
    {
        return "Chick-Fil-A";
    }
    else if (name == "CHIPOTLE")
    {
        return "Quesadilla";
    }
    else if (name == "FLAMEBROILER")
    {
        return "Flame Broiler";
    }
    else if (name == "HOTDOG")
    {
        return "Wienerschnitzel Hot Dog";
    }
    else if (name == "KFC")
    {
        return "KFC";
    }
    else if (name == "PICKUPSTIX")
    {
        return "Pick Up Stix";
    }
    else if (name == "PIZZA1" || name == "PIZZA2")
    {
        return "Pizza";
    }
    else if (name == "PORTOFINO1")
    {
        return "Portofino Pasta";
    }
    else if (name == "PORTOFINO2")
    {
        return "Portofino Pasta/Lasagna/Salad";
    }
    else if (name == "SMOOTHIE")
    {
        return "Smoothie &amp; Sandwich";
    }
    else if (name == "SUBWAY")
    {
        return "Subway";
    }
    else if (name == "TAQUITOS")
    {
        return "Taquitos";
    }
    else
    {
        return name;
    }

}

function buildLunchOrderTable(result)
{
    var total = result.orderTotal.toFixed(2);
    var name = result.studentName;
    var grade = result.studentGrade;
    var month = result.month;
    var sortedLunchMap = result.sortedLunchMap;

    /**
     * create the confirmation information on the page
     * @type {Array}
     */
    var buffer = [];
    buffer.push('<h2>' + month + ' Hot Lunch Order Confirmation</h2>');
    buffer.push('<h3>' + name + '</h3>');
    if (grade == 'F')
    {
        buffer.push('<h3>Faculty</h3>');
    }
    else if (grade == 'K')
    {
        buffer.push('<h3>Kindergarten</h3>');
    }
    else
    {
        buffer.push('<h3>Grade: ' + grade + '</h3>');
    }

    buffer.push('<h3>Order Total: $' + total + '</h3>');

    buffer.push('<fieldset>');
    buffer.push('<table class="table table-bordered table-striped">');
    buffer.push('<tbody class="body">');
    // key is the date
    for (var key in sortedLunchMap)
    {
        var order = sortedLunchMap[key];
        buffer.push('<tr>');
        buffer.push('<td>' + order.dateString + '</td>');
        buffer.push('<td>' + getPrettyName(order.itemTypeString) + '</td>');
        if (order.choice1 != null)
        {
            buffer.push('<td>' + getPrettyChoiceName(order.choice1, order.itemTypeString) + '</td>');
            //buffer.push('<td>' + order.choice1 + '</td>');
        }
        else
        {
            buffer.push('<td></td>');
        }
        if (order.choice2 != null)
        {
            buffer.push('<td>' + getPrettyChoiceName(order.choice2, order.itemTypeString) + '</td>');
        }
        else
        {
            buffer.push('<td></td>');
        }
        if (order.orderOne)
        {
            buffer.push('<td>Ordered</td>');
        }
        if (order.orderAdditional)
        {
            buffer.push('<td>Ordered Additional</td>');
        }
        buffer.push('</tr>');
    }
    buffer.push('</tbody>');
    buffer.push('</table>');
    buffer.push('</fieldset>');
    var bufferString = buffer.join('');

    // hide the title, lunch-form and submit button
    $('#lunch-form').hide();
    $('#submit').hide();
    $('#title').hide();

    //console.log('result: ' + bufferString);

    //$('#btn-print').show();
    $("#result").append(bufferString);

    /**
     * create the bottom section
     */
    var bottomBuffer = [];
    bottomBuffer.push('<p></p><p></p>');
    bottomBuffer.push('<p class="lead">Please submit the bottom portion of this sheet along with your check to the school</p>')
    bottomBuffer.push('<h1>-----------------------------------------------------------------------------------</h1>')
    bottomBuffer.push('<h2>' + month + ' Hot Lunch Order Confirmation</h2>');
    bottomBuffer.push('<p class="lead">For: ' + name + '</p>');
    if (grade == 'F')
    {
        bottomBuffer.push('<p class="lead">Faculty</p>');
    }
    if (grade == 'K')
    {
        bottomBuffer.push('<p class="lead">Kindergarten</p>');
    }
    else
    {
        bottomBuffer.push('<p class="lead">Grade: ' + grade + '</p>');
    }
    bottomBuffer.push('<p class="lead">Order Total: $' + total + '</p>');
    bottomBuffer.push('<p class="lead">Check Number:  ___________</p>')

    //$("#bottom").html(bottomBuffer);
    $("#bottom").append(bottomBuffer);

}
