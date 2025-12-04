Our group used AI to help us in Phase C:

We used it in step 4 to give us ideas for a transaction to use. 

Our prompt: "can you think of any transactions involving multiple tables i can create for a restruant review app" 
The output:

"""
Creating a New Restaurant with an Owner and Categories
This transaction ensures that a new restaurant, its owner association, and its category associations are all created successfully, or none of them are.
OperationTable(s)                                   InvolvedDescription
1. InsertRestaurant                                 Create the new restaurant record.
2. InsertPersonRestaurantOwnership                  Link the new restaurant to an existing owner (or a newly created one).
3. Insert (Multiple)RestaurantCategory              Link the new restaurant to one or more existing categories.
4. InsertMenu                                       Create the initial, empty menu for the restaurant.
"""

We decided on adopting the idea of incorporating a transactional workflow when inserting a new restaurant. 
Inserting a new restaurant requires us to update a few more tables, all of which should be encapsulated into one atomic action.
- Restaurant should contain the new restaurant,
- RestaurantCategory should use this new restaurant's RestaurantID and include the new restaurant's cuisines (categories), and
- Menu should add a new entry with the RestaurantID so that the restaurant owner has a menu to add menu items on when they are ready.
However, we decided not to include AI's idea of adding the restaurant owner in this atomic action, because in our restaurant review app,
we want users to be able to add new restaurants without knowing who the owner is (or incorrectly identifying who the owner is).



