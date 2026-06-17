#include <iostream>

struct User
{
    const char *name;
    int age;
    User **friends;
};

//@mite
extern "C" void celebrate_birthday_system(User *mainUser)
{
    if (mainUser == nullptr)
        return;

    mainUser->age += 1;

    if (mainUser->friends != nullptr && mainUser->friends[0] != nullptr)
    {
        User *firstFriend = mainUser->friends[0];

        firstFriend->age += 5;

        if (firstFriend->friends != nullptr && firstFriend->friends[0] != nullptr)
        {
            std::cout << "[C++ Native] " << firstFriend->name
                      << "'s first friend is indeed: " << firstFriend->friends[0]->name << std::endl;
        }
    }
}